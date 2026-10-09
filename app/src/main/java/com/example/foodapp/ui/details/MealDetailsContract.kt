package com.example.foodapp.ui.details

import com.example.foodapp.domain.model.MealModel
import com.example.foodapp.ui.util.UiText

data class MealDetailsState(
    val mealId: String = "",
    val isLoading: Boolean = false,
    val meal: MealModel? = null,
    val error: UiText? = null,
    val unitPriceCents: Int = 0,
    val quantity: Int = 1,
    val showFullRecipe: Boolean = false,
    /** Ingrédients que le client a retirés du plat (ex. « Harissa »). */
    val removedIngredients: Set<String> = emptySet()
) {
    val totalCents: Int get() = unitPriceCents * quantity
}

sealed interface MealDetailsIntent {
    data object LoadMeal : MealDetailsIntent
    data object IncrementQuantity : MealDetailsIntent
    data object DecrementQuantity : MealDetailsIntent
    data object ToggleRecipe : MealDetailsIntent
    data class ToggleIngredient(val ingredient: String) : MealDetailsIntent
    data object AddToCartClicked : MealDetailsIntent
    data object BackClicked : MealDetailsIntent
}

sealed interface MealDetailsEffect {
    data object NavigateToCart : MealDetailsEffect
    data object NavigateBack : MealDetailsEffect
}
