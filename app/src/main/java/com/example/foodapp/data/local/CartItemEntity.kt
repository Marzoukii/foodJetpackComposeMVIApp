package com.example.foodapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val mealId: String,
    val name: String,
    val thumbnail: String?,
    val unitPriceCents: Int,
    val quantity: Int,
    val addedAt: Long
)
