package com.example.foodapp.ui.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodapp.R
import com.example.foodapp.ui.components.AuthTextField
import com.example.foodapp.ui.components.GoogleButton
import com.example.foodapp.ui.components.OrDivider
import com.example.foodapp.ui.components.PrimaryButton
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.ui.theme.Spacing
import com.example.foodapp.ui.util.GoogleSignInResult
import com.example.foodapp.ui.util.MIN_PASSWORD_LENGTH
import com.example.foodapp.ui.util.requestGoogleIdToken
import com.example.foodapp.ui.util.uiText
import kotlinx.coroutines.launch

@Composable
fun RegisterRoute(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                RegisterEffect.NavigateToHome -> onNavigateToHome()
                RegisterEffect.NavigateToLogin -> onNavigateToLogin()
                RegisterEffect.LaunchGoogleSignIn -> scope.launch {
                    when (val result = requestGoogleIdToken(context)) {
                        is GoogleSignInResult.Success -> viewModel.onIntent(RegisterIntent.GoogleTokenReceived(result.idToken))
                        GoogleSignInResult.Cancelled -> viewModel.onIntent(RegisterIntent.GoogleFailed(null))
                        is GoogleSignInResult.Failure -> viewModel.onIntent(RegisterIntent.GoogleFailed(result.message))
                    }
                }
                is RegisterEffect.ShowMessage -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    scope.launch { snackbarHostState.showSnackbar(effect.message.asString(context)) }
                }
            }
        }
    }

    RegisterScreen(state = state, snackbarHostState = snackbarHostState, onIntent = viewModel::onIntent)
}

@Composable
fun RegisterScreen(
    state: RegisterState,
    snackbarHostState: SnackbarHostState,
    onIntent: (RegisterIntent) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current
    val enabled = !state.isLoading

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xxl, vertical = Spacing.xxxl),
            verticalArrangement = Arrangement.spacedBy(Spacing.l)
        ) {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge, color = colors.primary)
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s)) {
                Text(stringResource(R.string.register_title), style = MaterialTheme.typography.displayMedium)
                Text(
                    stringResource(R.string.register_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(Spacing.xs))

            AuthTextField(
                label = stringResource(R.string.register_name),
                value = state.name,
                onValueChange = { onIntent(RegisterIntent.NameChanged(it)) },
                placeholder = stringResource(R.string.register_name_placeholder),
                error = state.nameError?.asString(),
                capitalization = KeyboardCapitalization.Words,
                enabled = enabled
            )
            AuthTextField(
                label = stringResource(R.string.common_email),
                value = state.email,
                onValueChange = { onIntent(RegisterIntent.EmailChanged(it)) },
                placeholder = stringResource(R.string.common_email_placeholder),
                error = state.emailError?.asString(),
                keyboardType = KeyboardType.Email,
                enabled = enabled
            )
            AuthTextField(
                label = stringResource(R.string.common_password),
                value = state.password,
                onValueChange = { onIntent(RegisterIntent.PasswordChanged(it)) },
                placeholder = stringResource(R.string.register_password_min, MIN_PASSWORD_LENGTH),
                error = state.passwordError?.asString(),
                keyboardType = KeyboardType.Password,
                isPassword = true,
                passwordVisible = state.isPasswordVisible,
                onTogglePasswordVisibility = { onIntent(RegisterIntent.TogglePasswordVisibility) },
                enabled = enabled
            )
            AuthTextField(
                label = stringResource(R.string.register_confirm_password),
                value = state.confirmPassword,
                onValueChange = { onIntent(RegisterIntent.ConfirmPasswordChanged(it)) },
                placeholder = stringResource(R.string.register_confirm_password_placeholder),
                error = state.confirmPasswordError?.asString(),
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                onImeAction = {
                    focusManager.clearFocus()
                    onIntent(RegisterIntent.RegisterClicked)
                },
                isPassword = true,
                passwordVisible = state.isPasswordVisible,
                onTogglePasswordVisibility = { onIntent(RegisterIntent.TogglePasswordVisibility) },
                enabled = enabled
            )

            Spacer(Modifier.height(Spacing.xs))
            PrimaryButton(
                text = stringResource(if (state.isLoading) R.string.register_loading else R.string.register_submit),
                onClick = {
                    focusManager.clearFocus()
                    onIntent(RegisterIntent.RegisterClicked)
                },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            )
            OrDivider()
            GoogleButton(onClick = { onIntent(RegisterIntent.GoogleClicked) }, enabled = enabled)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.register_have_account), style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
                TextButton(onClick = { onIntent(RegisterIntent.LoginClicked) }) {
                    Text(stringResource(R.string.common_sign_in), style = MaterialTheme.typography.labelLarge, color = colors.primary)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun RegisterPreview() {
    FoodAppTheme {
        RegisterScreen(
            state = RegisterState(name = "Marie", confirmPasswordError = uiText(R.string.register_passwords_mismatch)),
            snackbarHostState = SnackbarHostState(),
            onIntent = {}
        )
    }
}
