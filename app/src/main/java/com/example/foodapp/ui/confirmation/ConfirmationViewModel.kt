package com.example.foodapp.ui.confirmation

import androidx.lifecycle.SavedStateHandle
import com.example.foodapp.navigation.Routes
import com.example.foodapp.ui.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConfirmationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : MviViewModel<ConfirmationState, ConfirmationIntent, ConfirmationEffect>(
    ConfirmationState(orderNumber = savedStateHandle.get<String>(Routes.ARG_ORDER_NUMBER).orEmpty())
) {

    override fun onIntent(intent: ConfirmationIntent) {
        when (intent) {
            ConfirmationIntent.BackToHomeClicked -> sendEffect(ConfirmationEffect.NavigateToHome)
        }
    }
}
