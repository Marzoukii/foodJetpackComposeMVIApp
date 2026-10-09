package com.example.foodapp.domain.usecase

import com.example.foodapp.data.repository.PreferencesRepository
import com.example.foodapp.domain.model.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/** Mode choisi sur l'écran de démarrage : livraison ou sur place. */
class GetOrderTypeUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    fun execute(): Flow<OrderType> = flow {
        preferencesRepository.getOrderType().collect {
            emit(it)
        }
    }
}

class GetTableNumberUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    fun execute(): Flow<Int?> = flow {
        preferencesRepository.getTableNumber().collect {
            emit(it)
        }
    }
}

class SaveOrderModeUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    fun execute(orderType: OrderType, tableNumber: Int?) {
        preferencesRepository.saveOrderMode(orderType, tableNumber)
    }
}
