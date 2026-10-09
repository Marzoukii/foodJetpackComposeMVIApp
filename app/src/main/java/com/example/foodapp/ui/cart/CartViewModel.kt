package com.example.foodapp.ui.cart

import androidx.lifecycle.viewModelScope
import com.example.foodapp.domain.usecase.ClearCartUseCase
import com.example.foodapp.domain.usecase.GetCartItemsUseCase
import com.example.foodapp.domain.usecase.GetOrderTypeUseCase
import com.example.foodapp.domain.usecase.UpdateCartQuantityUseCase
import com.example.foodapp.ui.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    getCartItemsUseCase: GetCartItemsUseCase,
    getOrderTypeUseCase: GetOrderTypeUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : MviViewModel<CartState, CartIntent, CartEffect>(CartState()) {

    init {
        getCartItemsUseCase.execute()
            .onEach { items -> setState { copy(isLoading = false, items = items) } }
            .launchIn(viewModelScope)

        getOrderTypeUseCase.execute()
            .onEach { type -> setState { copy(orderType = type) } }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: CartIntent) {
        when (intent) {
            is CartIntent.IncrementClicked -> changeQuantity(intent.lineId, +1)
            is CartIntent.DecrementClicked -> changeQuantity(intent.lineId, -1)
            CartIntent.ClearClicked -> viewModelScope.launch { clearCartUseCase.execute() }
            CartIntent.CheckoutClicked -> if (currentState.items.isNotEmpty()) sendEffect(CartEffect.NavigateToCheckout)
            CartIntent.BrowseMenuClicked -> sendEffect(CartEffect.NavigateToMenu)
            CartIntent.BackClicked -> sendEffect(CartEffect.NavigateBack)
        }
    }

    private fun changeQuantity(lineId: String, delta: Int) {
        val item = currentState.items.firstOrNull { it.lineId == lineId } ?: return
        viewModelScope.launch {
            updateCartQuantityUseCase.execute(lineId, item.quantity + delta)
        }
    }
}
