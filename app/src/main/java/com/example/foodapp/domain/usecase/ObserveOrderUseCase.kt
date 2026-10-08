package com.example.foodapp.domain.usecase

import com.example.foodapp.data.repository.OrderRepository
import com.example.foodapp.domain.model.OrderModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ObserveOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    fun execute(orderNumber: String): Flow<OrderModel?> = flow {
        orderRepository.observeOrder(orderNumber).collect {
            emit(it)
        }
    }
}
