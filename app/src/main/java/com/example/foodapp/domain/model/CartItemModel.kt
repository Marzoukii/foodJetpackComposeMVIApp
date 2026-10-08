package com.example.foodapp.domain.model

data class CartItemModel(
    val mealId: String,
    val name: String,
    val thumbnail: String?,
    val unitPriceCents: Int,
    val quantity: Int
) {
    val lineTotalCents: Int get() = unitPriceCents * quantity
}
