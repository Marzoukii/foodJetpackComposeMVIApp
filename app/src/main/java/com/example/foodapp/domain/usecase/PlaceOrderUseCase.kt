package com.example.foodapp.domain.usecase

import com.example.foodapp.data.repository.CartRepository
import javax.inject.Inject
import kotlin.random.Random

/** Pas de backend de commande : on génère un numéro et on vide le panier. */
class PlaceOrderUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend fun execute(): String {
        val orderNumber = Random.nextInt(100_000, 999_999).toString()
        cartRepository.clearCart()
        return orderNumber
    }
}
