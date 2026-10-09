package com.example.foodapp.domain.mapper

import com.example.foodapp.domain.model.OrderItemModel
import com.example.foodapp.domain.model.OrderModel
import com.example.foodapp.domain.model.OrderStatus
import com.example.foodapp.domain.model.OrderType
import com.google.firebase.database.DataSnapshot
import javax.inject.Inject

class OrderMapper @Inject constructor() {

    fun mapOrder(snapshot: DataSnapshot): OrderModel? {
        if (!snapshot.exists()) return null
        return OrderModel(
            orderNumber = snapshot.key.orEmpty(),
            status = OrderStatus.from(snapshot.child("status").getValue(String::class.java)),
            createdAt = snapshot.child("createdAt").getValue(Long::class.java) ?: 0L,
            statusHistory = snapshot.child("statusHistory").children.associate {
                OrderStatus.from(it.key) to (it.getValue(Long::class.java) ?: 0L)
            },
            orderType = OrderType.from(snapshot.child("orderType").getValue(String::class.java)),
            address = snapshot.child("address").getValue(String::class.java).orEmpty(),
            tableNumber = snapshot.child("tableNumber").getValue(Int::class.java),
            itemCount = snapshot.child("items").children.sumOf {
                it.child("quantity").getValue(Int::class.java) ?: 0
            },
            items = snapshot.child("items").children.map { item ->
                OrderItemModel(
                    name = item.child("name").getValue(String::class.java).orEmpty(),
                    quantity = item.child("quantity").getValue(Int::class.java) ?: 0,
                    removedIngredients = item.child("removedIngredients").children
                        .mapNotNull { it.getValue(String::class.java) }
                )
            },
            totalCents = snapshot.child("totalCents").getValue(Int::class.java) ?: 0
        )
    }

    fun mapOrders(snapshot: DataSnapshot): List<OrderModel> =
        snapshot.children.mapNotNull { mapOrder(it) }
}
