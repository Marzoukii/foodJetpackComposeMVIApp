package com.example.foodapp.domain.mapper

import com.example.foodapp.domain.model.OrderModel
import com.example.foodapp.domain.model.OrderStatus
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
            }
        )
    }
}
