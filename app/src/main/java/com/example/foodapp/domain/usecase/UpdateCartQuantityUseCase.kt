package com.example.foodapp.domain.usecase

import com.example.foodapp.data.repository.CartRepository
import javax.inject.Inject

class UpdateCartQuantityUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    /** Une quantité à 0 retire la ligne du panier. */
    suspend fun execute(lineId: String, quantity: Int) {
        cartRepository.updateQuantity(lineId, quantity)
    }
}
