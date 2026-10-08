package com.example.foodapp.domain.usecase

import com.example.foodapp.data.repository.CartRepository
import javax.inject.Inject

class UpdateCartQuantityUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    /** Une quantité à 0 retire le plat du panier. */
    suspend fun execute(mealId: String, quantity: Int) {
        cartRepository.updateQuantity(mealId, quantity)
    }
}
