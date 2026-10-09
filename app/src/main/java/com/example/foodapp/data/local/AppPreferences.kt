package com.example.foodapp.data.local

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("food_app_prefs", Context.MODE_PRIVATE)

    var onboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        set(value) = prefs.edit { putBoolean(KEY_ONBOARDING_COMPLETED, value) }

    private val _deliveryAddress = MutableStateFlow(prefs.getString(KEY_DELIVERY_ADDRESS, "").orEmpty())
    val deliveryAddress: StateFlow<String> = _deliveryAddress.asStateFlow()

    fun setDeliveryAddress(address: String) {
        prefs.edit { putString(KEY_DELIVERY_ADDRESS, address) }
        _deliveryAddress.value = address
    }

    /** Mode choisi au démarrage (nom de l'OrderType), null tant qu'aucun choix n'a été fait. */
    private val _orderType = MutableStateFlow(prefs.getString(KEY_ORDER_TYPE, null))
    val orderType: StateFlow<String?> = _orderType.asStateFlow()

    /** Numéro de table pour la commande sur place, 0 = non renseigné. */
    private val _tableNumber = MutableStateFlow(prefs.getInt(KEY_TABLE_NUMBER, 0))
    val tableNumber: StateFlow<Int> = _tableNumber.asStateFlow()

    fun setOrderMode(orderType: String, tableNumber: Int) {
        prefs.edit {
            putString(KEY_ORDER_TYPE, orderType)
            putInt(KEY_TABLE_NUMBER, tableNumber)
        }
        _orderType.value = orderType
        _tableNumber.value = tableNumber
    }

    private companion object {
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        const val KEY_DELIVERY_ADDRESS = "delivery_address"
        const val KEY_ORDER_TYPE = "order_type"
        const val KEY_TABLE_NUMBER = "table_number"
    }
}
