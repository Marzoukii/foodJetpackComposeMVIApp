package com.example.foodapp.data.repository

import com.example.foodapp.data.NetworkResult
import com.example.foodapp.domain.mapper.OrderMapper
import com.example.foodapp.domain.model.CartItemModel
import com.example.foodapp.domain.model.OrderModel
import com.example.foodapp.domain.model.OrderStatus
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

/**
 * Commandes dans Realtime Database :
 * - orders/{orderNumber} : la commande (status, statusHistory, articles...)
 * - userOrders/{uid}/{orderNumber} = true : index des commandes d'un utilisateur
 */
class OrderRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val orderMapper: OrderMapper
) {

    private val ordersRef get() = database.getReference(ORDERS)

    fun createOrder(
        orderNumber: String,
        userId: String,
        items: List<CartItemModel>,
        address: String,
        deliveryMode: String,
        paymentMethod: String,
        subtotalCents: Int,
        deliveryCents: Int
    ): Flow<NetworkResult<String?>> = flow {
        try {
            val order = mapOf(
                "userId" to userId,
                "status" to OrderStatus.RECEIVED.name,
                "createdAt" to ServerValue.TIMESTAMP,
                "statusHistory" to mapOf(OrderStatus.RECEIVED.name to ServerValue.TIMESTAMP),
                "address" to address,
                "deliveryMode" to deliveryMode,
                "paymentMethod" to paymentMethod,
                "subtotalCents" to subtotalCents,
                "deliveryCents" to deliveryCents,
                "totalCents" to subtotalCents + deliveryCents,
                "items" to items.map {
                    mapOf(
                        "mealId" to it.mealId,
                        "name" to it.name,
                        "thumbnail" to it.thumbnail,
                        "unitPriceCents" to it.unitPriceCents,
                        "quantity" to it.quantity
                    )
                }
            )
            // Écriture multi-chemins : la commande et l'index sont créés ensemble ou pas du tout.
            val updates = mapOf(
                "$ORDERS/$orderNumber" to order,
                "$USER_ORDERS/$userId/$orderNumber" to true
            )
            // Hors ligne, la tâche n'aboutit qu'au retour du réseau : on ne bloque pas l'écran indéfiniment.
            withTimeout(WRITE_TIMEOUT_MS) { database.reference.updateChildren(updates).await() }
            emit(NetworkResult.Success(orderNumber))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e))
        }
    }

    /** Émet la commande à chaque changement (statut mis à jour par le restaurant...). */
    fun observeOrder(orderNumber: String): Flow<OrderModel?> = callbackFlow {
        val ref = ordersRef.child(orderNumber)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(orderMapper.mapOrder(snapshot))
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    /** Côté restaurant / admin : passe la commande au statut suivant et date le changement. */
    fun updateStatus(orderNumber: String, status: OrderStatus): Flow<NetworkResult<Unit?>> = flow {
        try {
            val updates = mapOf(
                "status" to status.name,
                "statusHistory/${status.name}" to ServerValue.TIMESTAMP
            )
            ordersRef.child(orderNumber).updateChildren(updates).await()
            emit(NetworkResult.Success(Unit))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e))
        }
    }

    private companion object {
        const val ORDERS = "orders"
        const val USER_ORDERS = "userOrders"
        const val WRITE_TIMEOUT_MS = 15_000L
    }
}
