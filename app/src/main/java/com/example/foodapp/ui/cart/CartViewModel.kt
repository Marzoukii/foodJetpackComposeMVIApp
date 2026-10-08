package com.example.foodapp.ui.cart

import androidx.lifecycle.viewModelScope
import com.example.foodapp.domain.usecase.ClearCartUseCase
import com.example.foodapp.domain.usecase.GetCartItemsUseCase
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
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : MviViewModel<CartState, CartIntent, CartEffect>(CartState()) {

    init {
        getCartItemsUseCase.execute()
            .onEach { items -> setState { copy(isLoading = false, items = items) } }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: CartIntent) {
        when (intent) {
            is CartIntent.IncrementClicked -> changeQuantity(intent.mealId, +1)
            is CartIntent.DecrementClicked -> changeQuantity(intent.mealId, -1)
            CartIntent.ClearClicked -> viewModelScope.launch { clearCartUseCase.execute() }
            CartIntent.CheckoutClicked -> if (currentState.items.isNotEmpty()) sendEffect(CartEffect.NavigateToCheckout)
            CartIntent.BrowseMenuClicked -> sendEffect(CartEffect.NavigateToMenu)
            CartIntent.BackClicked -> sendEffect(CartEffect.NavigateBack)
        }
    }

    private fun changeQuantity(mealId: String, delta: Int) {
        val item = currentState.items.firstOrNull { it.mealId == mealId } ?: return
        viewModelScope.launch {
            updateCartQuantityUseCase.execute(mealId, item.quantity + delta)
        }
    }
}
