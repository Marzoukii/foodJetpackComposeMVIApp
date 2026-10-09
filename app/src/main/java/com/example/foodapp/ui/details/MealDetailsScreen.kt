package com.example.foodapp.ui.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodapp.R
import com.example.foodapp.domain.model.MealModel
import com.example.foodapp.ui.components.BoucheeIcons
import com.example.foodapp.ui.components.CircleIconButton
import com.example.foodapp.ui.components.ErrorView
import com.example.foodapp.ui.components.LoadingView
import com.example.foodapp.ui.components.MealImage
import com.example.foodapp.ui.components.PrimaryButton
import com.example.foodapp.ui.components.QuantityStepper
import com.example.foodapp.ui.components.StickyBottomBar
import com.example.foodapp.ui.components.Tag
import com.example.foodapp.ui.theme.Spacing
import com.example.foodapp.ui.util.areaLabel
import com.example.foodapp.ui.util.categoryLabel
import com.example.foodapp.ui.util.formatPrice

@Composable
fun MealDetailsRoute(
    onNavigateToCart: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: MealDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                MealDetailsEffect.NavigateToCart -> onNavigateToCart()
                MealDetailsEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    MealDetailsScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun MealDetailsScreen(
    state: MealDetailsState,
    onIntent: (MealDetailsIntent) -> Unit
) {
    Scaffold(
        bottomBar = {
            if (state.meal != null) {
                StickyBottomBar {
                    QuantityStepper(
                        quantity = state.quantity,
                        onDecrement = { onIntent(MealDetailsIntent.DecrementQuantity) },
                        onIncrement = { onIntent(MealDetailsIntent.IncrementQuantity) }
                    )
                    PrimaryButton(
                        text = stringResource(R.string.details_add_with_price, formatPrice(state.totalCents)),
                        onClick = { onIntent(MealDetailsIntent.AddToCartClicked) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    ) { padding ->
        when {
            state.isLoading -> LoadingView(Modifier.padding(padding))
            state.error != null -> Column(Modifier.padding(padding).statusBarsPadding()) {
                Box(Modifier.padding(Spacing.xl)) {
                    CircleIconButton(BoucheeIcons.Back, stringResource(R.string.common_back), onClick = { onIntent(MealDetailsIntent.BackClicked) })
                }
                ErrorView(message = state.error.asString(), onRetry = { onIntent(MealDetailsIntent.LoadMeal) })
            }
            state.meal != null -> MealDetailsContent(
                state = state,
                meal = state.meal,
                onIntent = onIntent,
                modifier = Modifier.padding(bottom = padding.calculateBottomPadding())
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MealDetailsContent(
    state: MealDetailsState,
    meal: MealModel,
    onIntent: (MealDetailsIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surface)
            .verticalScroll(rememberScrollState())
    ) {
        // Image du plat (330 dp) + boutons flottants
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
        ) {
            MealImage(url = meal.thumbnail, contentDescription = meal.name, modifier = Modifier.fillMaxSize())
            CircleIconButton(
                icon = BoucheeIcons.Back,
                contentDescription = stringResource(R.string.common_back),
                onClick = { onIntent(MealDetailsIntent.BackClicked) },
                bordered = false,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(Spacing.xl)
            )
        }

        // Feuille du détail (rayon 28) qui chevauche l'image
        Column(
            modifier = Modifier
                .offset(y = (-28).dp)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(colors.surface)
                .padding(start = Spacing.xl, end = Spacing.xl, top = 28.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.m), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(meal.name.orEmpty(), style = MaterialTheme.typography.headlineMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s)) {
                        areaLabel(meal.area)?.let { Tag(it, background = colors.secondaryContainer) }
                        categoryLabel(meal.category).takeIf { it.isNotBlank() }?.let {
                            Tag(it, background = colors.surfaceContainerHigh)
                        }
                    }
                }
                Text(
                    formatPrice(state.unitPriceCents),
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.primary,
                    maxLines = 1
                )
            }

            val instructions = meal.cleanInstructions()
            if (instructions.isNotBlank()) {
                Text(
                    text = instructions,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = MaterialTheme.typography.bodyLarge.fontSize * 0.94f),
                    color = colors.onSurfaceVariant,
                    maxLines = if (state.showFullRecipe) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            val ingredients = meal.ingredients()
            if (ingredients.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(stringResource(R.string.details_ingredients), style = MaterialTheme.typography.titleMedium.copy(fontSize = MaterialTheme.typography.bodyLarge.fontSize))
                    Text(
                        stringResource(R.string.details_ingredients_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                        verticalArrangement = Arrangement.spacedBy(Spacing.s)
                    ) {
                        ingredients.forEach { (ingredient, measure) ->
                            val removed = ingredient in state.removedIngredients
                            Surface(
                                onClick = { onIntent(MealDetailsIntent.ToggleIngredient(ingredient)) },
                                shape = CircleShape,
                                color = if (removed) colors.errorContainer else colors.surfaceContainer,
                                border = BorderStroke(1.dp, if (removed) colors.error else colors.outlineVariant)
                            ) {
                                Text(
                                    text = when {
                                        removed -> stringResource(R.string.common_without_ingredient, ingredient)
                                        measure.isBlank() -> ingredient
                                        else -> stringResource(R.string.details_ingredient_with_measure, ingredient, measure)
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        textDecoration = if (removed) TextDecoration.LineThrough else null
                                    ),
                                    color = if (removed) colors.onErrorContainer else colors.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (instructions.isNotBlank()) {
                TextButton(
                    onClick = { onIntent(MealDetailsIntent.ToggleRecipe) },
                    modifier = Modifier.offset(x = (-12).dp)
                ) {
                    Text(
                        stringResource(if (state.showFullRecipe) R.string.details_hide_recipe else R.string.details_show_recipe),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}
