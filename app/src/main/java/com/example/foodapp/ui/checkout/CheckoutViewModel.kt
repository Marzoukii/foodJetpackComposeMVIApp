package com.example.foodapp.ui.checkout

import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.NetworkResult
import com.example.foodapp.domain.usecase.GetCartItemsUseCase
import com.example.foodapp.domain.usecase.GetDeliveryAddressUseCase
import com.example.foodapp.domain.usecase.PlaceOrderUseCase
import com.example.foodapp.domain.usecase.SaveDeliveryAddressUseCase
import com.example.foodapp.ui.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    getCartItemsUseCase: GetCartItemsUseCase,
    getDeliveryAddressUseCase: GetDeliveryAddressUseCase,
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
            is CheckoutIntent.DeliveryModeSelected -> setState { copy(deliveryMode = intent.mode) }
            is CheckoutIntent.PaymentMethodSelected -> setState { copy(paymentMethod = intent.method) }
            CheckoutIntent.PayClicked -> placeOrder()
            CheckoutIntent.BackClicked -> sendEffect(CheckoutEffect.NavigateBack)
        }
    }

    private fun placeOrder() {
        val state = currentState
        when {
            state.isPlacingOrder -> return
            state.itemCount == 0 -> sendEffect(CheckoutEffect.ShowMessage("Votre panier est vide"))
            state.address.isBlank() -> {
                sendEffect(CheckoutEffect.ShowMessage("Ajoutez une adresse de livraison"))
                setState { copy(isEditingAddress = true, addressDraft = address) }
            }
            else -> {
                setState { copy(isPlacingOrder = true) }
                placeOrderUseCase.execute(
                    address = state.address,
                    deliveryMode = state.deliveryMode.name,
                    paymentMethod = state.paymentMethod.name,
                    deliveryCents = state.deliveryCents
                )
                    .onEach { result ->
                        when (result) {
                            is NetworkResult.Success -> sendEffect(CheckoutEffect.NavigateToConfirmation(result.data.orEmpty()))
                            is NetworkResult.Error -> {
                                setState { copy(isPlacingOrder = false) }
                                sendEffect(CheckoutEffect.ShowMessage("Impossible d'envoyer la commande, vérifiez votre connexion"))
                            }
                        }
                    }
                    .launchIn(viewModelScope)
            }
        }
    }
}
