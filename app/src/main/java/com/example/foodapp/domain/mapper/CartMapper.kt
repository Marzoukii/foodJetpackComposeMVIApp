package com.example.foodapp.domain.mapper

import com.example.foodapp.data.local.CartItemEntity
import com.example.foodapp.domain.model.CartItemModel
import javax.inject.Inject

class CartMapper @Inject constructor() {

    fun mapCartItems(entities: List<CartItemEntity>): List<CartItemModel> {
        return entities.map { mapCartItem(it) }
    }

    private fun mapCartItem(entity: CartItemEntity): CartItemModel {
        return CartItemModel(
            mealId = entity.mealId,
            name = entity.name,
            thumbnail = entity.thumbnail,
            unitPriceCents = entity.unitPriceCents,
            quantity = entity.quantity
        )
    }
}
