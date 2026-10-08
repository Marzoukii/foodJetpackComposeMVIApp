package com.example.foodapp.data.repository

import com.example.foodapp.data.local.AppPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PreferencesRepository @Inject constructor(
    private val appPreferences: AppPreferences
) {

    fun isOnboardingCompleted(): Boolean = appPreferences.onboardingCompleted

    fun completeOnboarding() {
        appPreferences.onboardingCompleted = true
    }

    fun getDeliveryAddress(): Flow<String> = appPreferences.deliveryAddress

    fun saveDeliveryAddress(address: String) {
        appPreferences.setDeliveryAddress(address.trim())
    }
}
