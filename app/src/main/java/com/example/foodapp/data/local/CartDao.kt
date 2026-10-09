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

    @Query("SELECT * FROM cart_items WHERE lineId = :lineId")
    suspend fun getById(lineId: String): CartItemEntity?

    @Upsert
    suspend fun upsert(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE lineId = :lineId")
    suspend fun updateQuantity(lineId: String, quantity: Int)

    @Query("DELETE FROM cart_items WHERE lineId = :lineId")
    suspend fun delete(lineId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clear()

    /** Ajoute la ligne, ou augmente sa quantité si le même plat (mêmes ingrédients retirés) y est déjà. */
    @Transaction
    suspend fun addOrIncrement(item: CartItemEntity) {
        val existing = getById(item.lineId)
        if (existing == null) {
            upsert(item)
        } else {
            updateQuantity(item.lineId, (existing.quantity + item.quantity).coerceAtMost(MAX_QUANTITY))
        }
    }

    companion object {
        const val MAX_QUANTITY = 20
    }
}
