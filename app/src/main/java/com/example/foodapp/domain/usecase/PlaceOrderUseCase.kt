package com.example.foodapp.domain.usecase

import com.example.foodapp.data.NetworkResult
import com.example.foodapp.data.repository.AuthRepository
import com.example.foodapp.data.repository.CartRepository
import com.example.foodapp.data.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.random.Random

/** Enregistre la commande dans Realtime Database (statut RECEIVED), puis vide le panier. */
class PlaceOrderUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) {
    fun execute(
        address: String,
        deliveryMode: String,
        paymentMethod: String,
        deliveryCents: Int
    ): Flow<NetworkResult<String?>> = flow {
        val user = authRepository.getCurrentUser()
        if (user == null) {
            emit(NetworkResult.Error(IllegalStateException("Utilisateur non connecté")))
            return@flow
        }
        val items = cartRepository.getCartItems().first()
        val orderNumber = Random.nextInt(100_000, 999_999).toString()
        orderRepository.createOrder(
            orderNumber = orderNumber,
            userId = user.id,
            items = items,
            address = address,
            deliveryMode = deliveryMode,
            paymentMethod = paymentMethod,
            subtotalCents = items.sumOf { it.lineTotalCents },
            deliveryCents = deliveryCents
        ).collect { result ->
            if (result is NetworkResult.Success) cartRepository.clearCart()
            emit(result)
        }
    }
}
