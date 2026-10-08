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

    private companion object {
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        const val KEY_DELIVERY_ADDRESS = "delivery_address"
    }
}
