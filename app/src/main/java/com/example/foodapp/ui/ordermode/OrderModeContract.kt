package com.example.foodapp.ui.ordermode

data class OrderModeState(
    val isTableDialogVisible: Boolean = false,
    val tableNumberDraft: String = "",
    val tableNumberError: String? = null,
    /** Ouverture de la session invitée en cours. */
    val isLoading: Boolean = false
)

sealed interface OrderModeIntent {
    data object DineInClicked : OrderModeIntent
    data object DeliveryClicked : OrderModeIntent
    data class TableNumberChanged(val value: String) : OrderModeIntent
    data object ConfirmTable : OrderModeIntent
    data object DismissTable : OrderModeIntent
}

sealed interface OrderModeEffect {
    data object NavigateToHome : OrderModeEffect
    /** Livraison sans compte : l'authentification est obligatoire. */
    data object NavigateToLogin : OrderModeEffect
    data class ShowMessage(val message: String) : OrderModeEffect
}
