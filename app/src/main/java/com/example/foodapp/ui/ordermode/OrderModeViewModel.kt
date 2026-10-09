package com.example.foodapp.ui.ordermode

import androidx.lifecycle.viewModelScope
import com.example.foodapp.R
import com.example.foodapp.data.NetworkResult
import com.example.foodapp.domain.model.OrderType
import com.example.foodapp.domain.usecase.GetCurrentUserUseCase
import com.example.foodapp.domain.usecase.GetTableNumberUseCase
import com.example.foodapp.domain.usecase.SaveOrderModeUseCase
import com.example.foodapp.domain.usecase.SignInAsGuestUseCase
import com.example.foodapp.ui.base.MviViewModel
import com.example.foodapp.ui.util.authErrorMessage
import com.example.foodapp.ui.util.uiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import javax.inject.Inject

@HiltViewModel
class OrderModeViewModel @Inject constructor(
    getTableNumberUseCase: GetTableNumberUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signInAsGuestUseCase: SignInAsGuestUseCase,
    private val saveOrderModeUseCase: SaveOrderModeUseCase
) : MviViewModel<OrderModeState, OrderModeIntent, OrderModeEffect>(OrderModeState()) {

    init {
        // Pré-remplit la dernière table utilisée.
        getTableNumberUseCase.execute()
            .take(1)
            .onEach { number -> setState { copy(tableNumberDraft = number?.toString().orEmpty()) } }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: OrderModeIntent) {
        when (intent) {
            OrderModeIntent.DineInClicked -> setState { copy(isTableDialogVisible = true, tableNumberError = null) }
            OrderModeIntent.DeliveryClicked -> chooseDelivery()
            is OrderModeIntent.TableNumberChanged -> {
                val digits = intent.value.filter { it.isDigit() }.take(TABLE_NUMBER_MAX_DIGITS)
                setState { copy(tableNumberDraft = digits, tableNumberError = null) }
            }
            OrderModeIntent.ConfirmTable -> chooseDineIn()
            OrderModeIntent.DismissTable -> setState { copy(isTableDialogVisible = false) }
        }
    }

    private fun chooseDelivery() {
        if (currentState.isLoading) return
        saveOrderModeUseCase.execute(OrderType.DELIVERY, null)
        val user = getCurrentUserUseCase.execute()
        // Une session invitée ne suffit pas : la livraison impose un vrai compte.
        sendEffect(if (user != null && !user.isAnonymous) OrderModeEffect.NavigateToHome else OrderModeEffect.NavigateToLogin)
    }

    private fun chooseDineIn() {
        val state = currentState
        if (state.isLoading) return
        val tableNumber = state.tableNumberDraft.toIntOrNull()?.takeIf { it > 0 }
        if (tableNumber == null) {
            setState { copy(tableNumberError = uiText(R.string.order_mode_table_required)) }
            return
        }
        saveOrderModeUseCase.execute(OrderType.DINE_IN, tableNumber)
        setState { copy(isTableDialogVisible = false) }

        // Déjà connecté (compte ou invité) : on garde la session. Sinon, session invitée sans inscription.
        if (getCurrentUserUseCase.execute() != null) {
            sendEffect(OrderModeEffect.NavigateToHome)
            return
        }
        signInAsGuestUseCase.execute()
            .onStart { setState { copy(isLoading = true) } }
            .onEach { result ->
                setState { copy(isLoading = false) }
                when (result) {
                    is NetworkResult.Success -> sendEffect(OrderModeEffect.NavigateToHome)
                    is NetworkResult.Error -> sendEffect(OrderModeEffect.ShowMessage(authErrorMessage(result.exception)))
                }
            }
            .launchIn(viewModelScope)
    }

    private companion object {
        const val TABLE_NUMBER_MAX_DIGITS = 3
    }
}
