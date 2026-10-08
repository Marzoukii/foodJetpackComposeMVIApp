package com.example.foodapp.domain.usecase

import com.example.foodapp.data.repository.CartRepository
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend fun execute(mealId: String, name: String, thumbnail: String?, quantity: Int = 1) {
        cartRepository.addToCart(mealId, name, thumbnail, quantity)
    }
}
