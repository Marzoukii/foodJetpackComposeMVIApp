package com.example.foodapp.domain.model

/** Statuts d'une commande, dans l'ordre du suivi. Stockés en base par leur `name` ("PREPARING"...). */
enum class OrderStatus {
    RECEIVED,
    PREPARING,
    ON_THE_WAY,
    DELIVERED;

    companion object {
        fun from(value: String?): OrderStatus = entries.firstOrNull { it.name == value } ?: RECEIVED
    }
}

data class OrderModel(
    val orderNumber: String,
    val status: OrderStatus,
    val createdAt: Long,
    val statusHistory: Map<OrderStatus, Long>
)
