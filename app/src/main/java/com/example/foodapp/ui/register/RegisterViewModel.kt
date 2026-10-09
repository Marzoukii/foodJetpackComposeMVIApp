package com.example.foodapp.ui.register

import androidx.lifecycle.viewModelScope
import com.example.foodapp.R
import com.example.foodapp.data.NetworkResult
import com.example.foodapp.domain.model.UserModel
import com.example.foodapp.domain.usecase.SignInWithGoogleUseCase
import com.example.foodapp.domain.usecase.SignUpUseCase
import com.example.foodapp.ui.base.MviViewModel
import com.example.foodapp.ui.util.MIN_PASSWORD_LENGTH
import com.example.foodapp.ui.util.UiText
import com.example.foodapp.ui.util.authErrorMessage
import com.example.foodapp.ui.util.isValidEmail
import com.example.foodapp.ui.util.uiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
) : MviViewModel<RegisterState, RegisterIntent, RegisterEffect>(RegisterState()) {

    override fun onIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.NameChanged -> setState { copy(name = intent.value, nameError = null) }
            is RegisterIntent.EmailChanged -> setState { copy(email = intent.value, emailError = null) }
            is RegisterIntent.PasswordChanged -> setState { copy(password = intent.value, passwordError = null) }
            is RegisterIntent.ConfirmPasswordChanged -> setState { copy(confirmPassword = intent.value, confirmPasswordError = null) }
            RegisterIntent.TogglePasswordVisibility -> setState { copy(isPasswordVisible = !isPasswordVisible) }
            RegisterIntent.RegisterClicked -> register()
            RegisterIntent.GoogleClicked -> {
                if (currentState.isLoading) return
                setState { copy(isLoading = true) }
                sendEffect(RegisterEffect.LaunchGoogleSignIn)
            }
            is RegisterIntent.GoogleTokenReceived -> authenticate(signInWithGoogleUseCase.execute(intent.idToken))
            is RegisterIntent.GoogleFailed -> {
                setState { copy(isLoading = false) }
                intent.message?.let { sendEffect(RegisterEffect.ShowMessage(UiText.Dynamic(it))) }
            }
            RegisterIntent.LoginClicked -> sendEffect(RegisterEffect.NavigateToLogin)
        }
    }

    private fun register() {
        val state = currentState
        if (state.isLoading) return
        val nameError = if (state.name.isBlank()) uiText(R.string.register_name_required) else null
        val emailError = when {
            state.email.isBlank() -> uiText(R.string.login_email_required)
            !isValidEmail(state.email) -> uiText(R.string.login_email_invalid)
            else -> null
        }
        val passwordError = if (state.password.length < MIN_PASSWORD_LENGTH) {
            uiText(R.string.register_password_min, MIN_PASSWORD_LENGTH)
        } else null
        val confirmPasswordError = if (state.confirmPassword != state.password) {
            uiText(R.string.register_passwords_mismatch)
        } else null

        if (listOf(nameError, emailError, passwordError, confirmPasswordError).any { it != null }) {
            setState {
                copy(
                    nameError = nameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                )
            }
            return
        }
        authenticate(signUpUseCase.execute(state.name, state.email, state.password))
    }

    private fun authenticate(request: Flow<NetworkResult<UserModel?>>) {
        request
            .onStart { setState { copy(isLoading = true) } }
            .onEach { result ->
                setState { copy(isLoading = false) }
                when (result) {
                    is NetworkResult.Success -> sendEffect(RegisterEffect.NavigateToHome)
                    is NetworkResult.Error -> sendEffect(RegisterEffect.ShowMessage(authErrorMessage(result.exception)))
                }
            }
            .launchIn(viewModelScope)
    }
}
