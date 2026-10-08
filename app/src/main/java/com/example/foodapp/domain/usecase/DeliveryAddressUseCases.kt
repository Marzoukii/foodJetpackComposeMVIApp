package com.example.foodapp.domain.usecase

import com.example.foodapp.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDeliveryAddressUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    fun execute(): Flow<String> = flow {
        preferencesRepository.getDeliveryAddress().collect {
            emit(it)
        }
    }
}

class SaveDeliveryAddressUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    fun execute(address: String) {
        preferencesRepository.saveDeliveryAddress(address)
    }
}
