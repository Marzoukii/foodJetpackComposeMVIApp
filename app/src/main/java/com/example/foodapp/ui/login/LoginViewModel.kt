package com.example.foodapp.ui.login

import androidx.lifecycle.viewModelScope
import com.example.foodapp.R
import com.example.foodapp.data.NetworkResult
import com.example.foodapp.domain.model.UserModel
import com.example.foodapp.domain.usecase.SendPasswordResetUseCase
import com.example.foodapp.domain.usecase.SignInUseCase
import com.example.foodapp.domain.usecase.SignInWithGoogleUseCase
import com.example.foodapp.ui.base.MviViewModel
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
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val sendPasswordResetUseCase: SendPasswordResetUseCase
) : MviViewModel<LoginState, LoginIntent, LoginEffect>(LoginState()) {

    override fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged -> setState { copy(email = intent.value, emailError = null) }
            is LoginIntent.PasswordChanged -> setState { copy(password = intent.value, passwordError = null) }
            LoginIntent.TogglePasswordVisibility -> setState { copy(isPasswordVisible = !isPasswordVisible) }
            LoginIntent.LoginClicked -> login()
            LoginIntent.ForgotPasswordClicked -> resetPassword()
            LoginIntent.GoogleClicked -> {
                if (currentState.isLoading) return
                setState { copy(isLoading = true) }
                sendEffect(LoginEffect.LaunchGoogleSignIn)
            }
            is LoginIntent.GoogleTokenReceived -> authenticate(signInWithGoogleUseCase.execute(intent.idToken))
            is LoginIntent.GoogleFailed -> {
                setState { copy(isLoading = false) }
                intent.message?.let { sendEffect(LoginEffect.ShowMessage(UiText.Dynamic(it))) }
            }
            LoginIntent.RegisterClicked -> sendEffect(LoginEffect.NavigateToRegister)
        }
    }

    private fun login() {
        val state = currentState
        if (state.isLoading) return
        val emailError = when {
            state.email.isBlank() -> uiText(R.string.login_email_required)
            !isValidEmail(state.email) -> uiText(R.string.login_email_invalid)
            else -> null
        }
        val passwordError = if (state.password.isEmpty()) uiText(R.string.login_password_required) else null
        if (emailError != null || passwordError != null) {
            setState { copy(emailError = emailError, passwordError = passwordError) }
            return
        }
        authenticate(signInUseCase.execute(state.email, state.password))
    }

    private fun authenticate(request: Flow<NetworkResult<UserModel?>>) {
        request
            .onStart { setState { copy(isLoading = true) } }
            .onEach { result ->
                setState { copy(isLoading = false) }
                when (result) {
                    is NetworkResult.Success -> sendEffect(LoginEffect.NavigateToHome)
                    is NetworkResult.Error -> sendEffect(LoginEffect.ShowMessage(authErrorMessage(result.exception)))
                }
            }
            .launchIn(viewModelScope)
    }

    private fun resetPassword() {
        val email = currentState.email
        if (!isValidEmail(email)) {
            setState { copy(emailError = uiText(R.string.login_email_required_for_reset)) }
            return
        }
        sendPasswordResetUseCase.execute(email)
            .onEach { result ->
                val message = when (result) {
                    is NetworkResult.Success -> uiText(R.string.login_reset_sent, email.trim())
                    is NetworkResult.Error -> authErrorMessage(result.exception)
                }
                sendEffect(LoginEffect.ShowMessage(message))
            }
            .launchIn(viewModelScope)
    }
}
