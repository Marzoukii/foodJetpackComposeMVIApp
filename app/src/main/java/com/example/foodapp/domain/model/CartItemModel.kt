package com.example.foodapp.domain.model

data class CartItemModel(
    val lineId: String,
    val mealId: String,
    val name: String,
    val thumbnail: String?,
    val unitPriceCents: Int,
    val quantity: Int,
    val removedIngredients: List<String> = emptyList()
) {
    val lineTotalCents: Int get() = unitPriceCents * quantity
}
