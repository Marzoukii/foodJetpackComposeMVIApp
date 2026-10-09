package com.example.foodapp.ui.util

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.foodapp.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

sealed interface GoogleSignInResult {
    data class Success(val idToken: String) : GoogleSignInResult
    data object Cancelled : GoogleSignInResult
    data class Failure(val message: String) : GoogleSignInResult
}

/**
 * Ouvre le sélecteur de compte Google (Credential Manager) et renvoie l'ID token
 * à passer à Firebase. Le [context] doit être l'Activity.
 */
suspend fun requestGoogleIdToken(context: Context): GoogleSignInResult {
    // Généré par le plugin google-services quand la connexion Google est activée dans Firebase.
    val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
    if (resId == 0) {
        return GoogleSignInResult.Failure(context.getString(R.string.google_error_not_configured))
    }

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(GetSignInWithGoogleOption.Builder(context.getString(resId)).build())
        .build()

    return try {
        val credential = CredentialManager.create(context).getCredential(context, request).credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            GoogleSignInResult.Success(GoogleIdTokenCredential.createFrom(credential.data).idToken)
        } else {
            GoogleSignInResult.Failure(context.getString(R.string.google_error_unknown_credential))
        }
    } catch (e: GetCredentialCancellationException) {
        GoogleSignInResult.Cancelled
    } catch (e: NoCredentialException) {
        GoogleSignInResult.Failure(context.getString(R.string.google_error_no_account))
    } catch (e: GetCredentialException) {
        GoogleSignInResult.Failure(e.message ?: context.getString(R.string.google_error_generic))
    }
}
