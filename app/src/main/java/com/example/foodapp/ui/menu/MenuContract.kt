package com.example.foodapp.ui.menu

import com.example.foodapp.domain.model.MealItemModel

data class MenuState(
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val isLoading: Boolean = false,
    val meals: List<MealItemModel> = emptyList(),
    val error: String? = null,
    val cartCount: Int = 0,
    val cartTotalCents: Int = 0
)

sealed interface MenuIntent {
    data object Retry : MenuIntent
    data class CategorySelected(val categoryName: String) : MenuIntent
    data class MealClicked(val mealId: String) : MenuIntent
    data class AddToCartClicked(val meal: MealItemModel) : MenuIntent
    data object CartClicked : MenuIntent
    data object BackClicked : MenuIntent
}

sealed interface MenuEffect {
    data class NavigateToMealDetails(val mealId: String) : MenuEffect
    data object NavigateToCart : MenuEffect
    data object NavigateBack : MenuEffect
    data class ShowMessage(val message: String) : MenuEffect
}
