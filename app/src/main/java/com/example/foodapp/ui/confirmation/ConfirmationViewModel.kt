package com.example.foodapp.ui.confirmation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.foodapp.domain.usecase.ObserveOrderUseCase
import com.example.foodapp.navigation.Routes
import com.example.foodapp.ui.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ConfirmationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeOrderUseCase: ObserveOrderUseCase
) : MviViewModel<ConfirmationState, ConfirmationIntent, ConfirmationEffect>(
    ConfirmationState(orderNumber = savedStateHandle.get<String>(Routes.ARG_ORDER_NUMBER).orEmpty())
) {

    init {
        // Le suivi se met à jour en direct quand le statut change dans Realtime Database.
        if (currentState.orderNumber.isNotEmpty()) {
            observeOrderUseCase.execute(currentState.orderNumber)
                .filterNotNull()
                .onEach { order ->
                    setState { copy(orderStatus = order.status, orderType = order.orderType, tableNumber = order.tableNumber) }
                }
                .catch { /* Lecture refusée ou coupée : on garde le dernier statut affiché. */ }
                .launchIn(viewModelScope)
        }
    }

    override fun onIntent(intent: ConfirmationIntent) {
        when (intent) {
            ConfirmationIntent.BackToHomeClicked -> sendEffect(ConfirmationEffect.NavigateToHome)
        }
    }
}
