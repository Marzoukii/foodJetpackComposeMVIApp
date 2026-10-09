package com.example.foodapp.ui.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodapp.domain.model.OrderType
import com.example.foodapp.ui.components.AuthTextField
import com.example.foodapp.ui.components.BackTopBar
import com.example.foodapp.ui.components.BoucheeIcons
import com.example.foodapp.ui.components.OutlinedCard
import com.example.foodapp.ui.components.PriceLine
import com.example.foodapp.ui.components.PriceSummary
import com.example.foodapp.ui.components.PrimaryButton
import com.example.foodapp.ui.components.StickyBottomBar
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.ui.theme.Spacing
import com.example.foodapp.ui.util.formatPrice
import kotlinx.coroutines.launch

@Composable
fun CheckoutRoute(
    onNavigateToConfirmation: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CheckoutEffect.NavigateToConfirmation -> onNavigateToConfirmation(effect.orderNumber)
                CheckoutEffect.NavigateToLogin -> onNavigateToLogin()
                CheckoutEffect.NavigateBack -> onNavigateBack()
                is CheckoutEffect.ShowMessage -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    scope.launch { snackbarHostState.showSnackbar(effect.message) }
                }
            }
        }
    }

    CheckoutScreen(state = state, snackbarHostState = snackbarHostState, onIntent = viewModel::onIntent)
}

@Composable
fun CheckoutScreen(
    state: CheckoutState,
    snackbarHostState: SnackbarHostState,
    onIntent: (CheckoutIntent) -> Unit
) {
    Scaffold(
        topBar = {
            BackTopBar(
                title = "Paiement",
                onBack = { onIntent(CheckoutIntent.BackClicked) },
                modifier = Modifier.statusBarsPadding()
            )
        },
        bottomBar = {
            StickyBottomBar {
                PrimaryButton(
                    text = "Payer ${formatPrice(state.totalCents)}",
                    onClick = { onIntent(CheckoutIntent.PayClicked) },
                    enabled = !state.isPlacingOrder,
                    modifier = Modifier.weight(1f)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(start = Spacing.xl, end = Spacing.xl, top = Spacing.s, bottom = Spacing.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl)
        ) {
            if (state.isDelivery) {
                Section("Adresse de livraison") {
                    if (state.isGuest) {
                        SignInCard(onSignIn = { onIntent(CheckoutIntent.SignInClicked) })
                    } else {
                        AddressCard(address = state.address, onEdit = { onIntent(CheckoutIntent.EditAddressClicked) })
                    }
                }

                Section("Livraison") {
                    SegmentedSelector(
                        options = DeliveryMode.entries,
                        selected = state.deliveryMode,
                        label = { it.label },
                        onSelect = { onIntent(CheckoutIntent.DeliveryModeSelected(it)) }
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.s)) {
                        Icon(BoucheeIcons.Clock, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        Text(
                            text = if (state.deliveryMode == DeliveryMode.Asap) {
                                "Livraison dès que la commande est prête"
                            } else {
                                "Le restaurant vous contactera pour fixer le créneau"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Section("Votre table") {
                    AuthTextField(
                        label = "Numéro de table",
                        value = state.tableNumber,
                        onValueChange = { onIntent(CheckoutIntent.TableNumberChanged(it)) },
                        placeholder = "Ex. 12",
                        error = state.tableNumberError,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.s)) {
                        Icon(BoucheeIcons.Table, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        Text(
                            "Le numéro est indiqué sur votre table",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Section("Moyen de paiement") {
                PaymentMethod.entries.forEach { method ->
                    PaymentOption(
                        method = method,
                        orderType = state.orderType,
                        icon = if (method == PaymentMethod.Card) BoucheeIcons.Card else BoucheeIcons.Cash,
                        selected = method == state.paymentMethod,
                        onClick = { onIntent(CheckoutIntent.PaymentMethodSelected(method)) }
                    )
                }
            }

            PriceSummary(
                lines = listOfNotNull(
                    PriceLine("${state.itemCount} article${if (state.itemCount > 1) "s" else ""}", formatPrice(state.subtotalCents)),
                    if (state.isDelivery) PriceLine("Livraison", formatPrice(state.deliveryCents)) else null
                ),
                totalLabel = "Total",
                total = formatPrice(state.totalCents)
            )
        }
    }

    if (state.isEditingAddress) {
        AddressDialog(
            value = state.addressDraft,
            onValueChange = { onIntent(CheckoutIntent.AddressDraftChanged(it)) },
            onConfirm = { onIntent(CheckoutIntent.ConfirmAddress) },
            onDismiss = { onIntent(CheckoutIntent.DismissAddress) }
        )
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium.copy(fontSize = MaterialTheme.typography.bodyLarge.fontSize))
        content()
    }
}

@Composable
private fun IconTile(icon: ImageVector, background: androidx.compose.ui.graphics.Color, tint: androidx.compose.ui.graphics.Color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(background)
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun AddressCard(address: String, onEdit: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = Spacing.l, end = Spacing.s, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconTile(BoucheeIcons.Pin, colors.primaryContainer, colors.primary)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Domicile", style = MaterialTheme.typography.titleSmall)
                Text(
                    address.ifBlank { "Aucune adresse renseignée" },
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }
            TextButton(onClick = onEdit) {
                Text(if (address.isBlank()) "Ajouter" else "Modifier", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            }
        }
    }
}

/** Invité en livraison : l'adresse demande un compte. */
@Composable
private fun SignInCard(onSignIn: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = Spacing.l, end = Spacing.s, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconTile(BoucheeIcons.User, colors.primaryContainer, colors.primary)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Compte requis", style = MaterialTheme.typography.titleSmall)
                Text(
                    "Connectez-vous pour être livré",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }
            TextButton(onClick = onSignIn) {
                Text("Se connecter", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            }
        }
    }
}

/** Sélecteur segmenté : fond surfaceContainerHigh, option active blanche. */
@Composable
private fun <T> SegmentedSelector(options: List<T>, selected: T, label: (T) -> String, onSelect: (T) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(colors.surfaceContainerHigh)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEach { mode ->
            val isSelected = mode == selected
            Surface(
                onClick = { onSelect(mode) },
                shape = MaterialTheme.shapes.small,
                color = if (isSelected) colors.surfaceContainer else colors.surfaceContainerHigh,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .then(if (isSelected) Modifier.shadow(1.dp, MaterialTheme.shapes.small) else Modifier)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        label(mode),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentOption(method: PaymentMethod, orderType: OrderType, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    OutlinedCard(
        onClick = onClick,
        border = if (selected) BorderStroke(2.dp, colors.primary) else BorderStroke(1.dp, colors.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.l, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconTile(icon, colors.surfaceContainerHigh, colors.onSurface)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(method.label, style = MaterialTheme.typography.titleSmall)
                Text(method.subtitle(orderType), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
            }
            // Bouton radio du design : anneau épais quand sélectionné
            Box(
                Modifier
                    .size(22.dp)
                    .border(
                        width = if (selected) 7.dp else 2.dp,
                        color = if (selected) colors.primary else colors.outlineVariant,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun AddressDialog(
    value: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adresse de livraison", style = MaterialTheme.typography.titleLarge) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text("N°, rue, ville") },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = value.isNotBlank()) { Text("Enregistrer") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun CheckoutPreview() {
    FoodAppTheme {
        CheckoutScreen(
            state = CheckoutState(address = "12 rue des Lilas, Lyon", itemCount = 4, subtotalCents = 4380),
            snackbarHostState = SnackbarHostState(),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun CheckoutDineInPreview() {
    FoodAppTheme {
        CheckoutScreen(
            state = CheckoutState(orderType = OrderType.DINE_IN, isGuest = true, tableNumber = "12", itemCount = 4, subtotalCents = 4380),
            snackbarHostState = SnackbarHostState(),
            onIntent = {}
        )
    }
}
