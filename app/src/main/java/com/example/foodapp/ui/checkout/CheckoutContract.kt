package com.example.foodapp.ui.checkout

import androidx.annotation.StringRes
import com.example.foodapp.R
import com.example.foodapp.domain.model.OrderType
import com.example.foodapp.domain.pricing.MealPricing
import com.example.foodapp.ui.util.UiText

enum class DeliveryMode(@StringRes val labelRes: Int) {
    Asap(R.string.checkout_delivery_asap),
    Scheduled(R.string.checkout_delivery_scheduled)
}

enum class PaymentMethod(@StringRes val labelRes: Int) {
    Card(R.string.checkout_payment_card),
    Cash(R.string.checkout_payment_cash);

    @StringRes
    fun subtitleRes(orderType: OrderType): Int {
        val atTable = orderType == OrderType.DINE_IN
        return when (this) {
            Card -> if (atTable) R.string.checkout_card_at_table else R.string.checkout_card_on_delivery
            Cash -> if (atTable) R.string.checkout_cash_at_table else R.string.checkout_cash_on_delivery
        }
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
    val tableNumberError: UiText? = null,
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
    data class ShowMessage(val message: UiText) : CheckoutEffect
}
