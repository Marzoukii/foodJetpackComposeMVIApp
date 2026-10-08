package com.example.foodapp.navigation

import android.net.Uri

object Routes {
    const val ARG_CATEGORY = "category"
    const val ARG_MEAL_ID = "mealId"
    const val ARG_ORDER_NUMBER = "orderNumber"

    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val MENU = "menu?$ARG_CATEGORY={$ARG_CATEGORY}"
    const val MEAL_DETAILS = "meal/{$ARG_MEAL_ID}"
    const val SEARCH = "search"
    const val CART = "cart"
    const val CHECKOUT = "checkout"
    const val CONFIRMATION = "confirmation/{$ARG_ORDER_NUMBER}"

    /** Sans catégorie, le menu ouvre la première catégorie. */
    fun menu(category: String? = null) =
        if (category == null) "menu" else "menu?$ARG_CATEGORY=${Uri.encode(category)}"

    fun mealDetails(mealId: String) = "meal/${Uri.encode(mealId)}"
    fun confirmation(orderNumber: String) = "confirmation/${Uri.encode(orderNumber)}"
}
