package com.example.foodapp.domain.usecase

import com.example.foodapp.data.repository.CartRepository
import javax.inject.Inject

class ClearCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend fun execute() {
        cartRepository.clearCart()
    }
}
