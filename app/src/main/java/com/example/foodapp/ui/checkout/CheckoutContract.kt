package com.example.foodapp.ui.checkout

import com.example.foodapp.domain.pricing.MealPricing

enum class DeliveryMode(val label: String) {
    Asap("Dès que possible"),
    Scheduled("Planifier")
}

enum class PaymentMethod(val label: String, val subtitle: String) {
    Card("Carte bancaire", "Par carte, à la livraison"),
    Cash("Espèces", "À la livraison")
}

data class CheckoutState(
    val address: String = "",
    val isEditingAddress: Boolean = false,
    val addressDraft: String = "",
    val deliveryMode: DeliveryMode = DeliveryMode.Asap,
    val paymentMethod: PaymentMethod = PaymentMethod.Card,
    val itemCount: Int = 0,
    val subtotalCents: Int = 0,
    val isPlacingOrder: Boolean = false
) {
    val deliveryCents: Int get() = MealPricing.DELIVERY_FEE_CENTS
    val totalCents: Int get() = subtotalCents + deliveryCents
}

sealed interface CheckoutIntent {
    data object EditAddressClicked : CheckoutIntent
    data class AddressDraftChanged(val value: String) : CheckoutIntent
    data object ConfirmAddress : CheckoutIntent
    data object DismissAddress : CheckoutIntent
    data class DeliveryModeSelected(val mode: DeliveryMode) : CheckoutIntent
    data class PaymentMethodSelected(val method: PaymentMethod) : CheckoutIntent
    data object PayClicked : CheckoutIntent
    data object BackClicked : CheckoutIntent
}

sealed interface CheckoutEffect {
    data class NavigateToConfirmation(val orderNumber: String) : CheckoutEffect
    data object NavigateBack : CheckoutEffect
    data class ShowMessage(val message: String) : CheckoutEffect
}
