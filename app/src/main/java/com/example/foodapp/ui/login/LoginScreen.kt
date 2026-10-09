package com.example.foodapp.ui.login

import com.example.foodapp.R
import androidx.compose.ui.res.stringResource
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodapp.ui.components.AuthTextField
import com.example.foodapp.ui.components.GoogleButton
import com.example.foodapp.ui.components.OrDivider
import com.example.foodapp.ui.components.PrimaryButton
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.ui.theme.Spacing
import com.example.foodapp.ui.util.GoogleSignInResult
import com.example.foodapp.ui.util.requestGoogleIdToken
import kotlinx.coroutines.launch

@Composable
fun LoginRoute(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LoginEffect.NavigateToHome -> onNavigateToHome()
                LoginEffect.NavigateToRegister -> onNavigateToRegister()
                LoginEffect.LaunchGoogleSignIn -> scope.launch {
                    when (val result = requestGoogleIdToken(context)) {
                        is GoogleSignInResult.Success -> viewModel.onIntent(LoginIntent.GoogleTokenReceived(result.idToken))
                        GoogleSignInResult.Cancelled -> viewModel.onIntent(LoginIntent.GoogleFailed(null))
                        is GoogleSignInResult.Failure -> viewModel.onIntent(LoginIntent.GoogleFailed(result.message))
                    }
                }
                is LoginEffect.ShowMessage -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    scope.launch { snackbarHostState.showSnackbar(effect.message) }
                }
            }
        }
    }

    LoginScreen(state = state, snackbarHostState = snackbarHostState, onIntent = viewModel::onIntent)
}

@Composable
fun LoginScreen(
    state: LoginState,
    snackbarHostState: SnackbarHostState,
    onIntent: (LoginIntent) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current

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
                Text("Content de vous revoir", style = MaterialTheme.typography.displayMedium)
                Text(
                    "Connectez-vous pour retrouver votre panier et vos commandes.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(Spacing.xs))

            AuthTextField(
                label = "E-mail",
                value = state.email,
                onValueChange = { onIntent(LoginIntent.EmailChanged(it)) },
                placeholder = "vous@exemple.fr",
                error = state.emailError,
                keyboardType = KeyboardType.Email,
                enabled = !state.isLoading
            )
            Column {
                AuthTextField(
                    label = "Mot de passe",
                    value = state.password,
                    onValueChange = { onIntent(LoginIntent.PasswordChanged(it)) },
                    placeholder = "Votre mot de passe",
                    error = state.passwordError,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    onImeAction = {
                        focusManager.clearFocus()
                        onIntent(LoginIntent.LoginClicked)
                    },
                    isPassword = true,
                    passwordVisible = state.isPasswordVisible,
                    onTogglePasswordVisibility = { onIntent(LoginIntent.TogglePasswordVisibility) },
                    enabled = !state.isLoading
                )
                TextButton(
                    onClick = { onIntent(LoginIntent.ForgotPasswordClicked) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Mot de passe oublié ?", style = MaterialTheme.typography.labelLarge, color = colors.primary)
                }
            }

            PrimaryButton(
                text = if (state.isLoading) "Connexion…" else "Se connecter",
                onClick = {
                    focusManager.clearFocus()
                    onIntent(LoginIntent.LoginClicked)
                },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            OrDivider()
            GoogleButton(onClick = { onIntent(LoginIntent.GoogleClicked) }, enabled = !state.isLoading)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pas encore de compte ?", style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
                TextButton(onClick = { onIntent(LoginIntent.RegisterClicked) }) {
                    Text("Créer un compte", style = MaterialTheme.typography.labelLarge, color = colors.primary)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun LoginPreview() {
    FoodAppTheme {
        LoginScreen(
            state = LoginState(email = "marie@exemple.fr", passwordError = "Saisissez votre mot de passe"),
            snackbarHostState = SnackbarHostState(),
            onIntent = {}
        )
    }
}
