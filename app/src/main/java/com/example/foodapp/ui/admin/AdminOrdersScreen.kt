package com.example.foodapp.ui.admin

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodapp.domain.model.OrderModel
import com.example.foodapp.domain.model.OrderStatus
import com.example.foodapp.ui.components.BackTopBar
import com.example.foodapp.ui.components.CategoryChip
import com.example.foodapp.ui.components.EmptyView
import com.example.foodapp.ui.components.ErrorView
import com.example.foodapp.ui.components.LoadingView
import com.example.foodapp.ui.components.OutlinedCard
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.ui.theme.Spacing
import com.example.foodapp.ui.util.formatPrice
import com.example.foodapp.ui.util.label
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminOrdersRoute(
    onNavigateBack: () -> Unit,
    viewModel: AdminOrdersViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AdminOrdersEffect.NavigateBack -> onNavigateBack()
                is AdminOrdersEffect.ShowMessage -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    scope.launch { snackbarHostState.showSnackbar(effect.message) }
                }
            }
        }
    }

    AdminOrdersScreen(state = state, snackbarHostState = snackbarHostState, onIntent = viewModel::onIntent)
}

@Composable
fun AdminOrdersScreen(
    state: AdminOrdersState,
    snackbarHostState: SnackbarHostState,
    onIntent: (AdminOrdersIntent) -> Unit
) {
    Scaffold(
        topBar = {
            BackTopBar(
                title = "Gestion des commandes",
                onBack = { onIntent(AdminOrdersIntent.BackClicked) },
                modifier = Modifier.statusBarsPadding()
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when {
            state.isLoading -> LoadingView(Modifier.padding(padding))
            state.error != null -> ErrorView(
                message = state.error,
                onRetry = { onIntent(AdminOrdersIntent.Retry) },
                modifier = Modifier.padding(padding)
            )
            state.orders.isEmpty() -> EmptyView(message = "Aucune commande pour le moment", modifier = Modifier.padding(padding))
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(start = Spacing.xl, end = Spacing.xl, top = Spacing.s, bottom = Spacing.l),
                verticalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                items(state.orders, key = { it.orderNumber }) { order ->
                    AdminOrderCard(
                        order = order,
                        onStatusSelected = { onIntent(AdminOrdersIntent.StatusSelected(order.orderNumber, it)) }
                    )
                }
            }
        }
    }
}

/** Une commande : numéro, heure, adresse, total et les statuts (le statut actuel est sélectionné). */
@Composable
private fun AdminOrderCard(order: OrderModel, onStatusSelected: (OrderStatus) -> Unit) {
    val colors = MaterialTheme.colorScheme
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.l), verticalArrangement = Arrangement.spacedBy(Spacing.s)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Commande n° ${order.orderNumber}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                Text(formatOrderDate(order.createdAt), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
            }
            if (order.address.isNotBlank()) {
                Text(order.address, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
            }
            Text(
                "${order.itemCount} article${if (order.itemCount > 1) "s" else ""} · ${formatPrice(order.totalCents)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(top = Spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                OrderStatus.entries.forEach { status ->
                    CategoryChip(
                        label = status.label,
                        selected = status == order.status,
                        onClick = { onStatusSelected(status) }
                    )
                }
            }
        }
    }
}

private fun formatOrderDate(timestamp: Long): String =
    if (timestamp == 0L) "" else SimpleDateFormat("dd/MM HH:mm", Locale.FRANCE).format(Date(timestamp))

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AdminOrdersPreview() {
    val order = OrderModel(
        orderNumber = "511438",
        status = OrderStatus.PREPARING,
        createdAt = 1_760_000_000_000,
        statusHistory = emptyMap(),
        address = "12 rue de la Paix, Paris",
        itemCount = 3,
        totalCents = 2450
    )
    FoodAppTheme {
        AdminOrdersScreen(
            state = AdminOrdersState(isLoading = false, orders = listOf(order, order.copy(orderNumber = "482913", status = OrderStatus.RECEIVED))),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = {}
        )
    }
}
