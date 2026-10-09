package com.example.foodapp.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodapp.domain.model.CartItemModel
import com.example.foodapp.ui.components.BackTopBar
import com.example.foodapp.ui.components.EmptyView
import com.example.foodapp.ui.components.LoadingView
import com.example.foodapp.ui.components.MealImage
import com.example.foodapp.ui.components.OutlinedCard
import com.example.foodapp.ui.components.PriceLine
import com.example.foodapp.ui.components.PriceSummary
import com.example.foodapp.ui.components.PrimaryButton
import com.example.foodapp.ui.components.QuantityStepper
import com.example.foodapp.ui.components.StepperSize
import com.example.foodapp.ui.components.StickyBottomBar
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.ui.theme.PriceTextStyle
import com.example.foodapp.ui.theme.Spacing
import com.example.foodapp.ui.util.formatPrice

@Composable
fun CartRoute(
    onNavigateToCheckout: () -> Unit,
    onNavigateToMenu: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                CartEffect.NavigateToCheckout -> onNavigateToCheckout()
                CartEffect.NavigateToMenu -> onNavigateToMenu()
                CartEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    CartScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun CartScreen(
    state: CartState,
    onIntent: (CartIntent) -> Unit
) {
    Scaffold(
        topBar = {
            BackTopBar(
                title = "Mon panier",
                onBack = { onIntent(CartIntent.BackClicked) },
                modifier = Modifier.statusBarsPadding()
            ) {
                if (state.items.isNotEmpty()) {
                    TextButton(onClick = { onIntent(CartIntent.ClearClicked) }) {
                        Text("Vider", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        },
        bottomBar = {
            if (state.items.isNotEmpty()) {
                StickyBottomBar {
                    PrimaryButton(
                        text = "Passer la commande · ${formatPrice(state.totalCents)}",
                        onClick = { onIntent(CartIntent.CheckoutClicked) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    ) { padding ->
        when {
            state.isLoading -> LoadingView(Modifier.padding(padding))
            state.items.isEmpty() -> EmptyView(
                message = "Votre panier est vide",
                actionLabel = "Voir le menu",
                onAction = { onIntent(CartIntent.BrowseMenuClicked) },
                modifier = Modifier.padding(padding)
            )
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(start = Spacing.xl, end = Spacing.xl, top = Spacing.s, bottom = Spacing.l),
                verticalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                items(state.items, key = { it.mealId }) { item ->
                    CartItemRow(
                        item = item,
                        onIncrement = { onIntent(CartIntent.IncrementClicked(item.mealId)) },
                        onDecrement = { onIntent(CartIntent.DecrementClicked(item.mealId)) }
                    )
                }
                item {
                    PriceSummary(
                        lines = listOfNotNull(
                            PriceLine("Sous-total", formatPrice(state.subtotalCents)),
                            if (state.isDelivery) PriceLine("Livraison", formatPrice(state.deliveryCents)) else null
                        ),
                        totalLabel = "Total",
                        total = formatPrice(state.totalCents),
                        modifier = Modifier.padding(top = Spacing.s)
                    )
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(item: CartItemModel, onIncrement: () -> Unit, onDecrement: () -> Unit) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(Spacing.m),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MealImage(
                url = item.thumbnail,
                contentDescription = item.name,
                modifier = Modifier
                    .size(72.dp)
                    .clip(MaterialTheme.shapes.small)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(item.name, style = MaterialTheme.typography.titleSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(formatPrice(item.lineTotalCents), style = PriceTextStyle.copy(fontSize = PriceTextStyle.fontSize * 0.94f))
            }
            QuantityStepper(
                quantity = item.quantity,
                onDecrement = onDecrement,
                onIncrement = onIncrement,
                size = StepperSize.Small
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun CartPreview() {
    FoodAppTheme {
        CartScreen(
            state = CartState(
                isLoading = false,
                items = listOf(
                    CartItemModel("52795", "Chicken Handi", null, 1350, 1),
                    CartItemModel("52771", "Spicy Arrabiata Penne", null, 1190, 2),
                    CartItemModel("52776", "Chocolate Gateau", null, 650, 1)
                )
            ),
            onIntent = {}
        )
    }
}
