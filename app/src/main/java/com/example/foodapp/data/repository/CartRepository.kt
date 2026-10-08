package com.example.foodapp.data.repository

import com.example.foodapp.data.local.CartDao
import com.example.foodapp.data.local.CartItemEntity
import com.example.foodapp.domain.mapper.CartMapper
import com.example.foodapp.domain.model.CartItemModel
import com.example.foodapp.domain.pricing.MealPricing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartRepository @Inject constructor(
    private val cartDao: CartDao,
    private val cartMapper: CartMapper
) {

    fun getCartItems(): Flow<List<CartItemModel>> =
        cartDao.observeAll().map { cartMapper.mapCartItems(it) }

    suspend fun addToCart(mealId: String, name: String, thumbnail: String?, quantity: Int) {
        cartDao.addOrIncrement(
            CartItemEntity(
                mealId = mealId,
                name = name,
                thumbnail = thumbnail,
                unitPriceCents = MealPricing.priceCentsFor(mealId),
                quantity = quantity,
                addedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateQuantity(mealId: String, quantity: Int) {
        if (quantity <= 0) {
            cartDao.delete(mealId)
        } else {
            cartDao.updateQuantity(mealId, quantity.coerceAtMost(CartDao.MAX_QUANTITY))
        }
    }

    suspend fun clearCart() {
        cartDao.clear()
    }
}
