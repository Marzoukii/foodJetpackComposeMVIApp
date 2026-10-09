package com.example.foodapp.ui.login

import com.example.foodapp.ui.util.UiText

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
    val isLoading: Boolean = false
)

sealed interface LoginIntent {
    data class EmailChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    data object TogglePasswordVisibility : LoginIntent
    data object LoginClicked : LoginIntent
    data object ForgotPasswordClicked : LoginIntent
    data object GoogleClicked : LoginIntent
    data class GoogleTokenReceived(val idToken: String) : LoginIntent
    data class GoogleFailed(val message: String?) : LoginIntent
    data object RegisterClicked : LoginIntent
}

sealed interface LoginEffect {
    data object NavigateToHome : LoginEffect
    data object NavigateToRegister : LoginEffect
    /** Le sélecteur de compte Google a besoin de l'Activity : il est lancé côté UI. */
    data object LaunchGoogleSignIn : LoginEffect
    data class ShowMessage(val message: UiText) : LoginEffect
}
