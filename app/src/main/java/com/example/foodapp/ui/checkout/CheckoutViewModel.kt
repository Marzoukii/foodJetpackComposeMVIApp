package com.example.foodapp.ui.checkout

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.foodapp.R
import com.example.foodapp.data.NetworkResult
import com.example.foodapp.domain.usecase.AuthenticationRequiredException
import com.example.foodapp.domain.usecase.GetAuthStateUseCase
import com.example.foodapp.domain.usecase.GetCartItemsUseCase
import com.example.foodapp.domain.usecase.GetDeliveryAddressUseCase
import com.example.foodapp.domain.usecase.GetOrderTypeUseCase
import com.example.foodapp.domain.usecase.GetTableNumberUseCase
import com.example.foodapp.domain.usecase.PlaceOrderUseCase
import com.example.foodapp.domain.usecase.SaveDeliveryAddressUseCase
import com.example.foodapp.ui.base.MviViewModel
import com.example.foodapp.ui.util.uiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    getCartItemsUseCase: GetCartItemsUseCase,
    getDeliveryAddressUseCase: GetDeliveryAddressUseCase,
    getAuthStateUseCase: GetAuthStateUseCase,
    getOrderTypeUseCase: GetOrderTypeUseCase,
    getTableNumberUseCase: GetTableNumberUseCase,
    private val saveDeliveryAddressUseCase: SaveDeliveryAddressUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase
) : MviViewModel<CheckoutState, CheckoutIntent, CheckoutEffect>(CheckoutState()) {

    init {
        getCartItemsUseCase.execute()
            .onEach { items ->
                // Le panier est vidé à la commande : on garde le récapitulatif affiché jusqu'à la navigation.
                if (!currentState.isPlacingOrder) {
                    setState {
                        copy(
                            itemCount = items.sumOf { it.quantity },
                            subtotalCents = items.sumOf { it.lineTotalCents }
                        )
                    }
                }
            }
            .launchIn(viewModelScope)

        getDeliveryAddressUseCase.execute()
            .onEach { address -> setState { copy(address = address) } }
            .launchIn(viewModelScope)

        getOrderTypeUseCase.execute()
            .onEach { type -> setState { copy(orderType = type) } }
            .launchIn(viewModelScope)

        // Table donnée au démarrage, modifiable ici avant de payer.
        getTableNumberUseCase.execute()
            .take(1)
            .onEach { number -> setState { copy(tableNumber = number?.toString().orEmpty()) } }
            .launchIn(viewModelScope)

        getAuthStateUseCase.execute()
            .onEach { user -> setState { copy(isGuest = user == null || user.isAnonymous) } }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: CheckoutIntent) {
        when (intent) {
            CheckoutIntent.EditAddressClicked -> setState { copy(isEditingAddress = true, addressDraft = address) }
            is CheckoutIntent.AddressDraftChanged -> setState { copy(addressDraft = intent.value) }
            CheckoutIntent.ConfirmAddress -> {
                saveDeliveryAddressUseCase.execute(currentState.addressDraft)
                setState { copy(isEditingAddress = false) }
            }
            CheckoutIntent.DismissAddress -> setState { copy(isEditingAddress = false) }
            is CheckoutIntent.TableNumberChanged -> {
                val digits = intent.value.filter { it.isDigit() }.take(TABLE_NUMBER_MAX_DIGITS)
                setState { copy(tableNumber = digits, tableNumberError = null) }
            }
            is CheckoutIntent.DeliveryModeSelected -> setState { copy(deliveryMode = intent.mode) }
            is CheckoutIntent.PaymentMethodSelected -> setState { copy(paymentMethod = intent.method) }
            CheckoutIntent.SignInClicked -> sendEffect(CheckoutEffect.NavigateToLogin)
            CheckoutIntent.PayClicked -> placeOrder()
            CheckoutIntent.BackClicked -> sendEffect(CheckoutEffect.NavigateBack)
        }
    }

    private fun placeOrder() {
        val state = currentState
        val tableNumber = state.tableNumber.toIntOrNull()?.takeIf { it > 0 }
        when {
            state.isPlacingOrder -> return
            state.itemCount == 0 -> sendEffect(CheckoutEffect.ShowMessage(uiText(R.string.cart_empty)))
            state.isDelivery && state.isGuest -> sendEffect(CheckoutEffect.NavigateToLogin)
            state.isDelivery && state.address.isBlank() -> {
                sendEffect(CheckoutEffect.ShowMessage(uiText(R.string.checkout_address_required)))
                setState { copy(isEditingAddress = true, addressDraft = address) }
            }
            !state.isDelivery && tableNumber == null -> setState { copy(tableNumberError = uiText(R.string.order_mode_table_required)) }
            else -> {
                setState { copy(isPlacingOrder = true) }
                placeOrderUseCase.execute(
                    orderType = state.orderType,
                    address = state.address,
                    tableNumber = tableNumber,
                    deliveryMode = state.deliveryMode.name,
                    paymentMethod = state.paymentMethod.name,
                    deliveryCents = state.deliveryCents
                )
                    .onEach { result ->
                        when (result) {
                            is NetworkResult.Success -> sendEffect(CheckoutEffect.NavigateToConfirmation(result.data.orEmpty()))
                            is NetworkResult.Error -> {
                                setState { copy(isPlacingOrder = false) }
                                if (result.exception is AuthenticationRequiredException) {
                                    sendEffect(CheckoutEffect.NavigateToLogin)
                                } else {
                                    Log.w(TAG, "Échec de l'envoi de la commande", result.exception)
                                    sendEffect(CheckoutEffect.ShowMessage(uiText(R.string.checkout_order_failed)))
                                }
                            }
                        }
                    }
                    .launchIn(viewModelScope)
            }
        }
    }

    private companion object {
        const val TAG = "Checkout"
        const val TABLE_NUMBER_MAX_DIGITS = 3
    }
}
