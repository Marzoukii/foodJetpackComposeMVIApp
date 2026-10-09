package com.example.foodapp.ui.search

import com.example.foodapp.domain.model.MealModel
import com.example.foodapp.ui.util.UiText

data class SearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<MealModel> = emptyList(),
    val hasSearched: Boolean = false,
    val error: UiText? = null
)

sealed interface SearchIntent {
    data class QueryChanged(val query: String) : SearchIntent
    data object Retry : SearchIntent
    data class MealClicked(val mealId: String) : SearchIntent
    data class AddToCartClicked(val meal: MealModel) : SearchIntent
    data object BackClicked : SearchIntent
}

sealed interface SearchEffect {
    data class NavigateToMealDetails(val mealId: String) : SearchEffect
    data object NavigateBack : SearchEffect
    data class ShowMessage(val message: UiText) : SearchEffect
}
