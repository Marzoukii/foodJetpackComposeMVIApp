package com.example.foodapp.ui.util

import android.util.Patterns
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
fun authErrorMessage(e: Exception): String = when (e) {
    // WeakPassword hérite de InvalidCredentials : il doit passer avant.
    is FirebaseAuthWeakPasswordException -> "Mot de passe trop faible ($MIN_PASSWORD_LENGTH caractères minimum)"
    is FirebaseAuthUserCollisionException -> "Un compte existe déjà avec cet e-mail"
    is FirebaseAuthInvalidUserException -> "Aucun compte actif ne correspond à cet e-mail"
    is FirebaseAuthInvalidCredentialsException -> "E-mail ou mot de passe incorrect"
    is FirebaseTooManyRequestsException -> "Trop de tentatives, réessayez dans quelques minutes"
    is FirebaseNetworkException -> "Pas de connexion internet"
    // Méthode de connexion désactivée dans la console Firebase (ex. connexion anonyme pour « Sur place »).
    is FirebaseAuthException if e.errorCode in DISABLED_PROVIDER_CODES ->
        "Ce mode de connexion n'est pas activé. Contactez le restaurant."
    else -> e.message ?: "Une erreur est survenue"
}
