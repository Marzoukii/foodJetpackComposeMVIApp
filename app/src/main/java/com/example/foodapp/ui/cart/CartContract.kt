package com.example.foodapp.ui.cart

import com.example.foodapp.domain.model.CartItemModel
import com.example.foodapp.domain.pricing.MealPricing

data class CartState(
    val isLoading: Boolean = true,
    val items: List<CartItemModel> = emptyList()
) {
    val subtotalCents: Int get() = items.sumOf { it.lineTotalCents }
    val deliveryCents: Int get() = MealPricing.DELIVERY_FEE_CENTS
    val totalCents: Int get() = subtotalCents + deliveryCents
}

sealed interface CartIntent {
    data class IncrementClicked(val mealId: String) : CartIntent
    data class DecrementClicked(val mealId: String) : CartIntent
    data object ClearClicked : CartIntent
    data object CheckoutClicked : CartIntent
    data object BrowseMenuClicked : CartIntent
    data object BackClicked : CartIntent
}

sealed interface CartEffect {
    data object NavigateToCheckout : CartEffect
    data object NavigateToMenu : CartEffect
    data object NavigateBack : CartEffect
}
