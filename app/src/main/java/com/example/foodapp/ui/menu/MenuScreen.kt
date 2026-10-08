package com.example.foodapp.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodapp.domain.model.MealItemModel
import com.example.foodapp.domain.pricing.MealPricing
import com.example.foodapp.ui.components.BackTopBar
import com.example.foodapp.ui.components.BottomTab
import com.example.foodapp.ui.components.BoucheeBottomBar
import com.example.foodapp.ui.components.CategoryChip
import com.example.foodapp.ui.components.EmptyView
import com.example.foodapp.ui.components.ErrorView
import com.example.foodapp.ui.components.LoadingView
import com.example.foodapp.ui.components.MealRowCard
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.ui.theme.Spacing
import com.example.foodapp.ui.util.categoryLabel
import com.example.foodapp.ui.util.formatPrice
import kotlinx.coroutines.launch

@Composable
fun MenuRoute(
    onNavigateToMealDetails: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateBack: () -> Unit,
    onTabSelected: (BottomTab) -> Unit,
    viewModel: MenuViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MenuEffect.NavigateToMealDetails -> onNavigateToMealDetails(effect.mealId)
                MenuEffect.NavigateToCart -> onNavigateToCart()
                MenuEffect.NavigateBack -> onNavigateBack()
                is MenuEffect.ShowMessage -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    scope.launch { snackbarHostState.showSnackbar(effect.message) }
                }
            }
        }
    }

    MenuScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
        onTabSelected = onTabSelected
    )
}

@Composable
fun MenuScreen(
    state: MenuState,
    snackbarHostState: SnackbarHostState,
    onIntent: (MenuIntent) -> Unit,
    onTabSelected: (BottomTab) -> Unit
) {
    Scaffold(
        topBar = {
            BackTopBar(
                title = categoryLabel(state.selectedCategory).ifBlank { "Menu" },
                onBack = { onIntent(MenuIntent.BackClicked) },
                modifier = Modifier.statusBarsPadding()
            )
        },
        bottomBar = {
            Column {
                if (state.cartCount > 0) {
                    CartBar(
                        count = state.cartCount,
                        totalCents = state.cartTotalCents,
                        onClick = { onIntent(MenuIntent.CartClicked) }
                    )
                }
                BoucheeBottomBar(BottomTab.Menu, state.cartCount, onTabSelected)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = Spacing.xl),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                modifier = Modifier.padding(bottom = Spacing.l)
            ) {
                items(state.categories) { category ->
                    CategoryChip(
                        label = categoryLabel(category),
                        selected = category == state.selectedCategory,
                        onClick = { onIntent(MenuIntent.CategorySelected(category)) }
                    )
                }
            }

            when {
                state.isLoading -> LoadingView()
                state.error != null -> ErrorView(message = state.error, onRetry = { onIntent(MenuIntent.Retry) })
                state.meals.isEmpty() -> EmptyView("Aucun plat dans cette catégorie")
                else -> LazyColumn(
                    contentPadding = PaddingValues(start = Spacing.xl, end = Spacing.xl, bottom = Spacing.l),
                    verticalArrangement = Arrangement.spacedBy(Spacing.m),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.meals, key = { it.id ?: it.hashCode() }) { meal ->
                        MealRowCard(
                            name = meal.name.orEmpty(),
                            subtitle = categoryLabel(state.selectedCategory),
                            price = formatPrice(MealPricing.priceCentsFor(meal.id)),
                            imageUrl = meal.thumbnail,
                            onClick = { meal.id?.let { onIntent(MenuIntent.MealClicked(it)) } },
                            onAddToCart = { onIntent(MenuIntent.AddToCartClicked(meal)) }
                        )
                    }
                }
            }
        }
    }
}

/** Barre sombre « 3 articles · 43,80 € [Voir le panier] ». */
@Composable
private fun CartBar(count: Int, totalCents: Int, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = colors.onSurface,
        contentColor = colors.surfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Spacing.xl, end = Spacing.xl, bottom = Spacing.m)
            .height(56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 22.dp, end = Spacing.s)
        ) {
            Text(
                text = "$count article${if (count > 1) "s" else ""} · ${formatPrice(totalCents)}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f)
            )
            Surface(shape = CircleShape, color = colors.primary, contentColor = colors.onPrimary) {
                Text(
                    "Voir le panier",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun MenuPreview() {
    FoodAppTheme {
        MenuScreen(
            state = MenuState(
                categories = listOf("Chicken", "Beef", "Pasta", "Dessert"),
                selectedCategory = "Chicken",
                meals = listOf(
                    MealItemModel("52795", "Chicken Handi", null),
                    MealItemModel("52772", "Teriyaki Chicken Casserole", null)
                ),
                cartCount = 3,
                cartTotalCents = 4380
            ),
            snackbarHostState = SnackbarHostState(),
            onIntent = {},
            onTabSelected = {}
        )
    }
}
