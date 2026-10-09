package com.example.foodapp.ui.ordermode

import com.example.foodapp.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodapp.ui.components.BoucheeIcons
import com.example.foodapp.ui.components.OutlinedCard
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.ui.theme.Spacing
import kotlinx.coroutines.launch

@Composable
fun OrderModeRoute(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: OrderModeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OrderModeEffect.NavigateToHome -> onNavigateToHome()
                OrderModeEffect.NavigateToLogin -> onNavigateToLogin()
                is OrderModeEffect.ShowMessage -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    scope.launch { snackbarHostState.showSnackbar(effect.message) }
                }
            }
        }
    }

    OrderModeScreen(state = state, snackbarHostState = snackbarHostState, onIntent = viewModel::onIntent)
}

/** Écran de démarrage : sur place (numéro de table, sans compte) ou livraison (compte obligatoire). */
@Composable
fun OrderModeScreen(
    state: OrderModeState,
    snackbarHostState: SnackbarHostState,
    onIntent: (OrderModeIntent) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xxl, vertical = Spacing.xxxl),
            verticalArrangement = Arrangement.spacedBy(Spacing.l)
        ) {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge, color = colors.primary)
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s)) {
                Text("Comment souhaitez-vous commander ?", style = MaterialTheme.typography.displayMedium)
                Text(
                    "Au restaurant, indiquez simplement votre table. Pour la livraison, un compte est nécessaire.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onSurfaceVariant
                )
            }

            ModeCard(
                icon = BoucheeIcons.Table,
                title = "Sur place",
                subtitle = "Je suis au restaurant, je donne mon numéro de table",
                enabled = !state.isLoading,
                onClick = { onIntent(OrderModeIntent.DineInClicked) }
            )
            ModeCard(
                icon = BoucheeIcons.Pin,
                title = "Livraison",
                subtitle = "Je me fais livrer (connexion requise)",
                enabled = !state.isLoading,
                onClick = { onIntent(OrderModeIntent.DeliveryClicked) }
            )
        }
    }

    if (state.isTableDialogVisible) {
        TableNumberDialog(
            value = state.tableNumberDraft,
            error = state.tableNumberError,
            onValueChange = { onIntent(OrderModeIntent.TableNumberChanged(it)) },
            onConfirm = { onIntent(OrderModeIntent.ConfirmTable) },
            onDismiss = { onIntent(OrderModeIntent.DismissTable) }
        )
    }
}

@Composable
private fun ModeCard(icon: ImageVector, title: String, subtitle: String, enabled: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    OutlinedCard(
        onClick = { if (enabled) onClick() },
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(Spacing.xl),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.l)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.primaryContainer)
            ) {
                Icon(icon, contentDescription = null, tint = colors.primary, modifier = Modifier.size(28.dp))
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun TableNumberDialog(
    value: String,
    error: String?,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Numéro de table", style = MaterialTheme.typography.titleLarge) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text("Ex. 12") },
                isError = error != null,
                supportingText = error?.let { { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onConfirm() }),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = value.isNotBlank()) { Text("Continuer") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun OrderModePreview() {
    FoodAppTheme {
        OrderModeScreen(state = OrderModeState(), snackbarHostState = SnackbarHostState(), onIntent = {})
    }
}
