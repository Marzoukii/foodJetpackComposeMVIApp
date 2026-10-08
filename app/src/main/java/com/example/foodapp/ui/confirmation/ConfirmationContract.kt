package com.example.foodapp.ui.confirmation

import com.example.foodapp.domain.model.OrderStatus

enum class StepStatus { Done, Current, Upcoming }

data class OrderStep(val label: String, val subtitle: String, val status: StepStatus)

data class ConfirmationState(
    val orderNumber: String = "",
    val orderStatus: OrderStatus = OrderStatus.RECEIVED
) {
    /** Les étapes avant le statut actuel sont faites ; une commande livrée est entièrement cochée. */
    val steps: List<OrderStep>
        get() = OrderStatus.entries.map { status ->
            val stepStatus = when {
                status < orderStatus || orderStatus == OrderStatus.DELIVERED -> StepStatus.Done
                status == orderStatus -> StepStatus.Current
                else -> StepStatus.Upcoming
            }
            OrderStep(status.label, status.subtitle, stepStatus)
        }
}

private val OrderStatus.label: String
    get() = when (this) {
        OrderStatus.RECEIVED -> "Commande reçue"
        OrderStatus.PREPARING -> "En préparation"
        OrderStatus.ON_THE_WAY -> "En route"
        OrderStatus.DELIVERED -> "Livrée"
    }

private val OrderStatus.subtitle: String
    get() = when (this) {
        OrderStatus.RECEIVED -> "Le restaurant a bien reçu votre commande"
        OrderStatus.PREPARING -> "Vos plats sont en cuisine"
        OrderStatus.ON_THE_WAY -> "Un livreur récupère la commande"
        OrderStatus.DELIVERED -> "Bon appétit !"
    }

sealed interface ConfirmationIntent {
    data object BackToHomeClicked : ConfirmationIntent
}

sealed interface ConfirmationEffect {
    data object NavigateToHome : ConfirmationEffect
}
