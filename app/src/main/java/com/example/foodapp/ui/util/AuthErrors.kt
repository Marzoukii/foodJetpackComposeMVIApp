package com.example.foodapp.ui.util

import android.util.Patterns
import com.example.foodapp.R
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

const val MIN_PASSWORD_LENGTH = 6

private val DISABLED_PROVIDER_CODES = setOf("ERROR_ADMIN_RESTRICTED_OPERATION", "ERROR_OPERATION_NOT_ALLOWED")

fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

/** Traduit les exceptions Firebase Auth en message lisible. */
fun authErrorMessage(e: Exception): UiText = when (e) {
    // WeakPassword hérite de InvalidCredentials : il doit passer avant.
    is FirebaseAuthWeakPasswordException -> uiText(R.string.auth_error_weak_password, MIN_PASSWORD_LENGTH)
    is FirebaseAuthUserCollisionException -> uiText(R.string.auth_error_email_in_use)
    is FirebaseAuthInvalidUserException -> uiText(R.string.auth_error_no_account)
    is FirebaseAuthInvalidCredentialsException -> uiText(R.string.auth_error_invalid_credentials)
    is FirebaseTooManyRequestsException -> uiText(R.string.auth_error_too_many_requests)
    is FirebaseNetworkException -> uiText(R.string.auth_error_no_network)
    // Méthode de connexion désactivée dans la console Firebase (ex. connexion anonyme pour « Sur place »).
    is FirebaseAuthException if e.errorCode in DISABLED_PROVIDER_CODES -> uiText(R.string.auth_error_provider_disabled)
    else -> e.toUiText(R.string.common_generic_error)
}
