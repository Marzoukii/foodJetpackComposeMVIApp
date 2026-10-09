package com.example.foodapp.ui.admin

import com.example.foodapp.domain.model.OrderModel
import com.example.foodapp.domain.model.OrderStatus
import com.example.foodapp.ui.util.UiText

data class AdminOrdersState(
    val isLoading: Boolean = true,
    val error: UiText? = null,
    val orders: List<OrderModel> = emptyList()
)

sealed interface AdminOrdersIntent {
    data class StatusSelected(val orderNumber: String, val status: OrderStatus) : AdminOrdersIntent
    data object Retry : AdminOrdersIntent
    data object TeamClicked : AdminOrdersIntent
    data object BackClicked : AdminOrdersIntent
}

sealed interface AdminOrdersEffect {
    data object NavigateToTeam : AdminOrdersEffect
    data object NavigateBack : AdminOrdersEffect
    data class ShowMessage(val message: UiText) : AdminOrdersEffect
}
