package com.example.foodapp.ui.checkout

import com.example.foodapp.domain.model.OrderType
import com.example.foodapp.domain.pricing.MealPricing

enum class DeliveryMode(val label: String) {
    Asap("Dès que possible"),
    Scheduled("Planifier")
}

enum class PaymentMethod(val label: String) {
    Card("Carte bancaire"),
    Cash("Espèces");

    fun subtitle(orderType: OrderType): String {
        val moment = if (orderType == OrderType.DINE_IN) "à table" else "à la livraison"
        return if (this == Card) "Par carte, $moment" else moment.replaceFirstChar { it.uppercase() }
    }
}

data class CheckoutState(
    /** Choisi sur l'écran de démarrage. */
    val orderType: OrderType = OrderType.DELIVERY,
    /** Session invitée : la livraison demande de se connecter. */
    val isGuest: Boolean = false,
    val address: String = "",
    val isEditingAddress: Boolean = false,
    val addressDraft: String = "",
    val tableNumber: String = "",
    val tableNumberError: String? = null,
    val deliveryMode: DeliveryMode = DeliveryMode.Asap,
    val paymentMethod: PaymentMethod = PaymentMethod.Card,
    val itemCount: Int = 0,
    val subtotalCents: Int = 0,
    val isPlacingOrder: Boolean = false
) {
    val isDelivery: Boolean get() = orderType == OrderType.DELIVERY
    val deliveryCents: Int get() = if (isDelivery) MealPricing.DELIVERY_FEE_CENTS else 0
    val totalCents: Int get() = subtotalCents + deliveryCents
}

sealed interface CheckoutIntent {
    data object EditAddressClicked : CheckoutIntent
    data class AddressDraftChanged(val value: String) : CheckoutIntent
    data object ConfirmAddress : CheckoutIntent
    data object DismissAddress : CheckoutIntent
    data class TableNumberChanged(val value: String) : CheckoutIntent
    data class DeliveryModeSelected(val mode: DeliveryMode) : CheckoutIntent
    data class PaymentMethodSelected(val method: PaymentMethod) : CheckoutIntent
    data object SignInClicked : CheckoutIntent
    data object PayClicked : CheckoutIntent
    data object BackClicked : CheckoutIntent
}

sealed interface CheckoutEffect {
    data class NavigateToConfirmation(val orderNumber: String) : CheckoutEffect
    /** Livraison en invité : connexion, puis retour au paiement. */
    data object NavigateToLogin : CheckoutEffect
    data object NavigateBack : CheckoutEffect
    data class ShowMessage(val message: String) : CheckoutEffect
}
