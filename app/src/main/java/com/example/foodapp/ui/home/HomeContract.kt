package com.example.foodapp.ui.home

import com.example.foodapp.domain.model.CategoryModel
import com.example.foodapp.domain.model.MealItemModel
import com.example.foodapp.domain.model.MealModel
import com.example.foodapp.domain.model.UserModel

data class HomeState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val deliveryAddress: String = "",
    val categories: List<CategoryModel> = emptyList(),
    /** Plat du jour (random.php). */
    val mealOfTheDay: MealModel? = null,
    /** Plats de la première catégorie, affichés en « Populaires ». */
    val popularCategory: String? = null,
    val popularMeals: List<MealItemModel> = emptyList(),
    val cartCount: Int = 0,
    val user: UserModel? = null,
    val isAccountDialogVisible: Boolean = false
)

sealed interface HomeIntent {
    data object Load : HomeIntent
    data class CategoryClicked(val categoryName: String) : HomeIntent
    data object SeeAllCategoriesClicked : HomeIntent
    data class MealClicked(val mealId: String) : HomeIntent
    data object SearchClicked : HomeIntent
    data object CartClicked : HomeIntent
    data object AccountClicked : HomeIntent
    data object DismissAccountDialog : HomeIntent
    data object SignOutClicked : HomeIntent
}

sealed interface HomeEffect {
    data class NavigateToMenu(val categoryName: String?) : HomeEffect
    data class NavigateToMealDetails(val mealId: String) : HomeEffect
    data object NavigateToSearch : HomeEffect
    data object NavigateToCart : HomeEffect
    data object NavigateToLogin : HomeEffect
}
