package com.example.foodapp.domain.usecase

import com.example.foodapp.data.NetworkResult
import com.example.foodapp.data.repository.AuthRepository
import com.example.foodapp.data.repository.CartRepository
import com.example.foodapp.data.repository.OrderRepository
import com.example.foodapp.domain.model.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/** Livraison demandée sans vrai compte : l'utilisateur doit se connecter. */
class AuthenticationRequiredException : IllegalStateException("Connexion requise pour la livraison")

/** Enregistre la commande dans Realtime Database (statut RECEIVED), puis vide le panier. */
class PlaceOrderUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) {
    /** Livraison : [address] obligatoire. Sur place : [tableNumber] obligatoire, sans frais. */
    fun execute(
        orderType: OrderType,
        address: String,
        tableNumber: Int?,
        deliveryMode: String,
        paymentMethod: String,
        deliveryCents: Int
    ): Flow<NetworkResult<String?>> = flow {
        val user = authRepository.getCurrentUser()
        if (user == null || (orderType == OrderType.DELIVERY && user.isAnonymous)) {
            emit(NetworkResult.Error(AuthenticationRequiredException()))
            return@flow
        }
        val isDelivery = orderType == OrderType.DELIVERY
        val items = cartRepository.getCartItems().first()
        orderRepository.createOrder(
            userId = user.id,
            items = items,
            orderType = orderType,
            address = if (isDelivery) address else "",
            tableNumber = if (isDelivery) null else tableNumber,
            deliveryMode = deliveryMode,
            paymentMethod = paymentMethod,
            subtotalCents = items.sumOf { it.lineTotalCents },
            deliveryCents = if (isDelivery) deliveryCents else 0
        ).collect { result ->
            if (result is NetworkResult.Success) cartRepository.clearCart()
            emit(result)
        }
    }
}
