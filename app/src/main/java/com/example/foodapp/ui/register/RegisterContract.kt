package com.example.foodapp.ui.register

data class RegisterState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false
)

sealed interface RegisterIntent {
    data class NameChanged(val value: String) : RegisterIntent
    data class EmailChanged(val value: String) : RegisterIntent
    data class PasswordChanged(val value: String) : RegisterIntent
    data class ConfirmPasswordChanged(val value: String) : RegisterIntent
    data object TogglePasswordVisibility : RegisterIntent
    data object RegisterClicked : RegisterIntent
    data object GoogleClicked : RegisterIntent
    data class GoogleTokenReceived(val idToken: String) : RegisterIntent
    data class GoogleFailed(val message: String?) : RegisterIntent
    data object LoginClicked : RegisterIntent
}

sealed interface RegisterEffect {
    data object NavigateToHome : RegisterEffect
    data object NavigateToLogin : RegisterEffect
    data object LaunchGoogleSignIn : RegisterEffect
    data class ShowMessage(val message: String) : RegisterEffect
}
