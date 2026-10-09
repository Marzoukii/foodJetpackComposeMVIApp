package com.example.foodapp.ui.util

import androidx.annotation.StringRes
import com.example.foodapp.R
import com.example.foodapp.domain.model.OrderStatus
import com.example.foodapp.domain.model.OrderType

@StringRes
fun OrderStatus.labelRes(type: OrderType): Int = when (this) {
    OrderStatus.RECEIVED -> R.string.status_received
    OrderStatus.PREPARING -> R.string.status_preparing
    OrderStatus.ON_THE_WAY -> R.string.status_on_the_way
    OrderStatus.DELIVERED -> if (type == OrderType.DINE_IN) R.string.status_served else R.string.status_delivered
}

@get:StringRes
val OrderStatus.subtitleRes: Int
    get() = when (this) {
        OrderStatus.RECEIVED -> R.string.status_received_subtitle
        OrderStatus.PREPARING -> R.string.status_preparing_subtitle
        OrderStatus.ON_THE_WAY -> R.string.status_on_the_way_subtitle
        OrderStatus.DELIVERED -> R.string.status_delivered_subtitle
    }

@get:StringRes
val OrderType.labelRes: Int
    get() = when (this) {
        OrderType.DELIVERY -> R.string.order_type_delivery
        OrderType.DINE_IN -> R.string.order_type_dine_in
    }
