package com.example.foodapp.ui.admin

import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.NetworkResult
import com.example.foodapp.domain.model.OrderStatus
import com.example.foodapp.domain.usecase.ObserveAllOrdersUseCase
import com.example.foodapp.domain.usecase.UpdateOrderStatusUseCase
import com.example.foodapp.ui.base.MviViewModel
import com.example.foodapp.ui.util.label
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class AdminOrdersViewModel @Inject constructor(
    private val observeAllOrdersUseCase: ObserveAllOrdersUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase
) : MviViewModel<AdminOrdersState, AdminOrdersIntent, AdminOrdersEffect>(AdminOrdersState()) {

    private var ordersJob: Job? = null

    init {
        observeOrders()
    }

    override fun onIntent(intent: AdminOrdersIntent) {
        when (intent) {
            is AdminOrdersIntent.StatusSelected -> updateStatus(intent.orderNumber, intent.status)
            AdminOrdersIntent.Retry -> observeOrders()
            AdminOrdersIntent.BackClicked -> sendEffect(AdminOrdersEffect.NavigateBack)
        }
    }

    /** La liste se met à jour en direct (nouvelles commandes, statuts changés ailleurs). */
    private fun observeOrders() {
        ordersJob?.cancel()
        ordersJob = observeAllOrdersUseCase.execute()
            .onStart { setState { copy(isLoading = true, error = null) } }
            .onEach { orders -> setState { copy(isLoading = false, orders = orders) } }
            .catch {
                setState { copy(isLoading = false, error = "Accès refusé : ce compte n'est pas administrateur") }
            }
            .launchIn(viewModelScope)
    }

    private fun updateStatus(orderNumber: String, status: OrderStatus) {
        val order = currentState.orders.firstOrNull { it.orderNumber == orderNumber } ?: return
        if (order.status == status) return
        updateOrderStatusUseCase.execute(orderNumber, status)
            .onEach { result ->
                when (result) {
                    is NetworkResult.Success ->
                        sendEffect(AdminOrdersEffect.ShowMessage("Commande n° $orderNumber : ${status.label(order.orderType)}"))
                    is NetworkResult.Error ->
                        sendEffect(AdminOrdersEffect.ShowMessage("Impossible de changer le statut"))
                }
            }
            .launchIn(viewModelScope)
    }
}
