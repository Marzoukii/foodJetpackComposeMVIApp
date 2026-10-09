package com.example.foodapp.ui.confirmation

import com.example.foodapp.domain.model.OrderStatus
import com.example.foodapp.domain.model.OrderType
import com.example.foodapp.ui.util.label
import com.example.foodapp.ui.util.subtitle

enum class StepStatus { Done, Current, Upcoming }

data class OrderStep(val label: String, val subtitle: String, val status: StepStatus)

data class ConfirmationState(
    val orderNumber: String = "",
    val orderStatus: OrderStatus = OrderStatus.RECEIVED,
    val orderType: OrderType = OrderType.DELIVERY,
    val tableNumber: Int? = null
) {
    /** Les étapes avant le statut actuel sont faites ; une commande livrée est entièrement cochée. */
    val steps: List<OrderStep>
        get() = orderType.statuses.map { status ->
            val stepStatus = when {
                status < orderStatus || orderStatus == OrderStatus.DELIVERED -> StepStatus.Done
                status == orderStatus -> StepStatus.Current
                else -> StepStatus.Upcoming
            }
            OrderStep(status.label(orderType), status.subtitle, stepStatus)
        }
}

sealed interface ConfirmationIntent {
    data object BackToHomeClicked : ConfirmationIntent
}

sealed interface ConfirmationEffect {
    data object NavigateToHome : ConfirmationEffect
}
