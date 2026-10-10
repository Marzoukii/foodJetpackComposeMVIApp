package com.example.foodapp.data.repository

import com.example.foodapp.data.local.AppPreferences
import com.example.foodapp.domain.model.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesRepository @Inject constructor(
    private val appPreferences: AppPreferences
) {

    fun getDeliveryAddress(): Flow<String> = appPreferences.deliveryAddress

    fun saveDeliveryAddress(address: String) {
        appPreferences.setDeliveryAddress(address.trim())
    }

    fun getOrderType(): Flow<OrderType> = appPreferences.orderType.map { OrderType.from(it) }

    fun getTableNumber(): Flow<Int?> = appPreferences.tableNumber.map { it.takeIf { number -> number > 0 } }

    fun saveOrderMode(orderType: OrderType, tableNumber: Int?) {
        appPreferences.setOrderMode(orderType.name, tableNumber ?: 0)
    }
}
