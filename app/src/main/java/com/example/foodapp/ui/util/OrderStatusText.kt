package com.example.foodapp.ui.util

import com.example.foodapp.domain.model.OrderStatus
import com.example.foodapp.domain.model.OrderType

fun OrderStatus.label(type: OrderType): String = when (this) {
    OrderStatus.RECEIVED -> "Commande reçue"
    OrderStatus.PREPARING -> "En préparation"
    OrderStatus.ON_THE_WAY -> "En route"
    OrderStatus.DELIVERED -> if (type == OrderType.DINE_IN) "Servie" else "Livrée"
}

val OrderStatus.subtitle: String
    get() = when (this) {
        OrderStatus.RECEIVED -> "Le restaurant a bien reçu votre commande"
        OrderStatus.PREPARING -> "Vos plats sont en cuisine"
        OrderStatus.ON_THE_WAY -> "Un livreur récupère la commande"
        OrderStatus.DELIVERED -> "Bon appétit !"
    }

val OrderType.label: String
    get() = when (this) {
        OrderType.DELIVERY -> "Livraison"
        OrderType.DINE_IN -> "Sur place"
    }
