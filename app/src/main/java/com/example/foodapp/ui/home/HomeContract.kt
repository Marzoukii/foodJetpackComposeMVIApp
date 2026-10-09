package com.example.foodapp.ui.home

import com.example.foodapp.domain.model.CategoryModel
import com.example.foodapp.domain.model.MealItemModel
import com.example.foodapp.domain.model.MealModel
import com.example.foodapp.domain.model.OrderType
import com.example.foodapp.domain.model.UserModel
import com.example.foodapp.ui.util.UiText

data class HomeState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val deliveryAddress: String = "",
    val orderType: OrderType = OrderType.DELIVERY,
    val tableNumber: Int? = null,
    val categories: List<CategoryModel> = emptyList(),
    /** Plat du jour (random.php). */
    val mealOfTheDay: MealModel? = null,
    /** Catégorie entourée : la première au démarrage, puis celle que l'utilisateur touche. */
    val selectedCategory: String? = null,
    /** Catégorie des plats affichés en « Populaires » (rejoint selectedCategory une fois chargée). */
    val popularCategory: String? = null,
    val popularMeals: List<MealItemModel> = emptyList(),
    val cartCount: Int = 0,
    val user: UserModel? = null,
    val isAccountDialogVisible: Boolean = false,
    /** Compte présent dans admins/{uid} : accès à la gestion des commandes. */
    val isAdmin: Boolean = false
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
    data object ChangeOrderModeClicked : HomeIntent
    data object AdminOrdersClicked : HomeIntent
}

sealed interface HomeEffect {
    data class NavigateToMenu(val categoryName: String?) : HomeEffect
    data class NavigateToMealDetails(val mealId: String) : HomeEffect
    data object NavigateToSearch : HomeEffect
    data object NavigateToCart : HomeEffect
    /** Écran de démarrage : changement de mode (retour possible) ou déconnexion (pile vidée). */
    data class NavigateToOrderMode(val clearBackStack: Boolean) : HomeEffect
    data object NavigateToAdminOrders : HomeEffect
}
