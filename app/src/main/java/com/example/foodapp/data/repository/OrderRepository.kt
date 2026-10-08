package com.example.foodapp.data.repository

import com.example.foodapp.data.NetworkResult
import com.example.foodapp.domain.mapper.OrderMapper
import com.example.foodapp.domain.model.CartItemModel
import com.example.foodapp.domain.model.OrderModel
import com.example.foodapp.domain.model.OrderStatus
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import kotlin.random.Random

/**
 * Commandes dans Realtime Database :
 * - orders/{orderNumber} : la commande (status, statusHistory, articles...)
 * - userOrders/{uid}/{orderNumber} = true : index des commandes d'un utilisateur
 * - admins/{uid} = true : comptes autorisés à changer les statuts (ajoutés à la main dans la console)
 */
class OrderRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val orderMapper: OrderMapper
) {

    private val ordersRef get() = database.getReference(ORDERS)

    /** Crée la commande au statut RECEIVED et émet son numéro. */
    fun createOrder(
        userId: String,
        items: List<CartItemModel>,
        address: String,
        deliveryMode: String,
        paymentMethod: String,
        subtotalCents: Int,
        deliveryCents: Int
    ): Flow<NetworkResult<String?>> = flow {
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

        repeat(MAX_ATTEMPTS) { attempt ->
            val orderNumber = Random.nextInt(100_000, 999_999).toString()
            // Écriture multi-chemins : la commande et l'index sont créés ensemble ou pas du tout.
            val updates = mapOf(
                "$ORDERS/$orderNumber" to order,
                "$USER_ORDERS/$userId/$orderNumber" to true
            )
            try {
                withTimeout(WRITE_TIMEOUT_MS) { database.reference.updateChildren(updates).await() }
                emit(NetworkResult.Success(orderNumber))
                return@flow
            } catch (e: TimeoutCancellationException) {
                // Hors ligne, Firebase garderait l'écriture en attente et l'enverrait plus tard,
                // alors que l'utilisateur a vu un échec : on l'annule.
                database.purgeOutstandingWrites()
                emit(NetworkResult.Error(e))
                return@flow
            } catch (e: DatabaseException) {
                // Les règles refusent d'écraser une commande existante : numéro déjà pris, on en tire un autre.
                if (attempt == MAX_ATTEMPTS - 1) {
                    emit(NetworkResult.Error(e))
                    return@flow
                }
            } catch (e: Exception) {
                emit(NetworkResult.Error(e))
                return@flow
            }
        }
    }

    /** Émet la commande à chaque changement (statut mis à jour par le restaurant...). */
    fun observeOrder(orderNumber: String): Flow<OrderModel?> =
        ordersRef.child(orderNumber).observe { orderMapper.mapOrder(it) }

    /** Admin : les dernières commandes, de la plus récente à la plus ancienne. */
    fun observeAllOrders(): Flow<List<OrderModel>> =
        ordersRef.orderByChild("createdAt").limitToLast(ADMIN_ORDERS_LIMIT)
            .observe { orderMapper.mapOrders(it).reversed() }

    fun observeIsAdmin(userId: String): Flow<Boolean> =
        database.getReference(ADMINS).child(userId)
            .observe { it.getValue(Boolean::class.java) == true }

    /** Admin : change le statut et date le changement dans statusHistory. */
    fun updateStatus(orderNumber: String, status: OrderStatus): Flow<NetworkResult<Unit?>> = flow {
        try {
            val updates = mapOf(
                "status" to status.name,
                "statusHistory/${status.name}" to ServerValue.TIMESTAMP
            )
            withTimeout(WRITE_TIMEOUT_MS) { ordersRef.child(orderNumber).updateChildren(updates).await() }
            emit(NetworkResult.Success(Unit))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e))
        }
    }

    private fun <T> Query.observe(map: (DataSnapshot) -> T): Flow<T> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(map(snapshot))
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        addValueEventListener(listener)
        awaitClose { removeEventListener(listener) }
    }

    private companion object {
        const val ORDERS = "orders"
        const val USER_ORDERS = "userOrders"
        const val ADMINS = "admins"
        const val MAX_ATTEMPTS = 3
        const val WRITE_TIMEOUT_MS = 15_000L
        const val ADMIN_ORDERS_LIMIT = 50
    }
}
