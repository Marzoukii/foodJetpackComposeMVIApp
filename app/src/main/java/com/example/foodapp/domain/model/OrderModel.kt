package com.example.foodapp.domain.model

import com.example.foodapp.domain.enums.OrderStatus
import com.example.foodapp.domain.enums.OrderType

data class OrderModel(
    val orderNumber: String,
    val status: OrderStatus,
    val createdAt: Long,
    val statusHistory: Map<OrderStatus, Long>,
    val orderType: OrderType,
    val address: String,
    val tableNumber: Int?,
    val itemCount: Int,
    val items: List<OrderItemModel> = emptyList(),
    val totalCents: Int
)

/** Un article d'une commande, avec les ingrédients que le client a retirés (pour la cuisine). */
data class OrderItemModel(
    val name: String,
    val quantity: Int,
    val removedIngredients: List<String> = emptyList()
)
