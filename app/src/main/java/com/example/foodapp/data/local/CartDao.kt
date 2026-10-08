package com.example.foodapp.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_items ORDER BY addedAt")
    fun observeAll(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE mealId = :mealId")
    suspend fun getById(mealId: String): CartItemEntity?

    @Upsert
    suspend fun upsert(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE mealId = :mealId")
    suspend fun updateQuantity(mealId: String, quantity: Int)

    @Query("DELETE FROM cart_items WHERE mealId = :mealId")
    suspend fun delete(mealId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clear()

    /** Ajoute le plat, ou augmente sa quantité s'il est déjà dans le panier. */
    @Transaction
    suspend fun addOrIncrement(item: CartItemEntity) {
        val existing = getById(item.mealId)
        if (existing == null) {
            upsert(item)
        } else {
            updateQuantity(item.mealId, (existing.quantity + item.quantity).coerceAtMost(MAX_QUANTITY))
        }
    }

    companion object {
        const val MAX_QUANTITY = 20
    }
}
