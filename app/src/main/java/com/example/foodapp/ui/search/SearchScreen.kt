package com.example.foodapp.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodapp.domain.pricing.MealPricing
import com.example.foodapp.ui.components.BackTopBar
import com.example.foodapp.ui.components.EmptyView
import com.example.foodapp.ui.components.ErrorView
import com.example.foodapp.ui.components.LoadingView
import com.example.foodapp.ui.components.MealRowCard
import com.example.foodapp.ui.components.SearchField
import com.example.foodapp.ui.theme.Spacing
import com.example.foodapp.ui.util.areaLabel
import com.example.foodapp.ui.util.categoryLabel
import com.example.foodapp.ui.util.formatPrice
import kotlinx.coroutines.launch

@Composable
fun SearchRoute(
    onNavigateToMealDetails: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SearchEffect.NavigateToMealDetails -> onNavigateToMealDetails(effect.mealId)
                SearchEffect.NavigateBack -> onNavigateBack()
                is SearchEffect.ShowMessage -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    scope.launch { snackbarHostState.showSnackbar(effect.message) }
                }
            }
        }
    }

    SearchScreen(state = state, snackbarHostState = snackbarHostState, onIntent = viewModel::onIntent)
}

@Composable
fun SearchScreen(
    state: SearchState,
    snackbarHostState: SnackbarHostState,
    onIntent: (SearchIntent) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Scaffold(
        topBar = {
            BackTopBar(
                title = "Rechercher",
                onBack = { onIntent(SearchIntent.BackClicked) },
                modifier = Modifier.statusBarsPadding()
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchField(
                value = state.query,
                onValueChange = { onIntent(SearchIntent.QueryChanged(it)) },
                modifier = Modifier
                    .padding(horizontal = Spacing.xl)
                    .padding(bottom = Spacing.l),
                textFieldModifier = Modifier.focusRequester(focusRequester)
            )

            when {
                state.isLoading -> LoadingView()
                state.error != null -> ErrorView(message = state.error, onRetry = { onIntent(SearchIntent.Retry) })
                !state.hasSearched -> EmptyView("Tapez le nom d'un plat pour lancer la recherche")
                state.results.isEmpty() -> EmptyView("Aucun résultat pour « ${state.query} »")
                else -> LazyColumn(
                    contentPadding = PaddingValues(start = Spacing.xl, end = Spacing.xl, bottom = Spacing.l),
                    verticalArrangement = Arrangement.spacedBy(Spacing.m),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.results, key = { it.id ?: it.hashCode() }) { meal ->
                        MealRowCard(
                            name = meal.name.orEmpty(),
                            subtitle = listOfNotNull(
                                areaLabel(meal.area),
                                categoryLabel(meal.category).takeIf { it.isNotBlank() }
                            ).joinToString(" · "),
                            price = formatPrice(MealPricing.priceCentsFor(meal.id)),
                            imageUrl = meal.thumbnail,
                            onClick = { meal.id?.let { onIntent(SearchIntent.MealClicked(it)) } },
                            onAddToCart = { onIntent(SearchIntent.AddToCartClicked(meal)) }
                        )
                    }
                }
            }
        }
    }
}
