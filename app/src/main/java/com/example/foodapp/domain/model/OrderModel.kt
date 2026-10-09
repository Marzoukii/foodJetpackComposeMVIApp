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

/**
 * Livraison : compte obligatoire, adresse et frais de livraison.
 * Sur place : numéro de table, sans compte (session anonyme) ni frais.
 */
enum class OrderType {
    DELIVERY,
    DINE_IN;

    /** Statuts proposés pour ce type : une commande sur place n'est jamais « en route ». */
    val statuses: List<OrderStatus>
        get() = if (this == DINE_IN) OrderStatus.entries - OrderStatus.ON_THE_WAY else OrderStatus.entries

    companion object {
        // Les commandes créées avant l'ajout du type sont des livraisons.
        fun from(value: String?): OrderType = entries.firstOrNull { it.name == value } ?: DELIVERY
    }
}

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
