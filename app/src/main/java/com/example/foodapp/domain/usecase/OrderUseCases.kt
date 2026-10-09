package com.example.foodapp.domain.usecase

import com.example.foodapp.data.NetworkResult
import com.example.foodapp.data.repository.AuthRepository
import com.example.foodapp.data.repository.OrderRepository
import com.example.foodapp.domain.model.OrderModel
import com.example.foodapp.domain.model.OrderStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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

class ObserveAllOrdersUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    fun execute(): Flow<List<OrderModel>> = flow {
        orderRepository.observeAllOrders().collect {
            emit(it)
        }
    }
}

/** Suit le compte connecté : true s'il figure dans admins/{uid}. */
class ObserveIsAdminUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun execute(): Flow<Boolean> = authRepository.getAuthState()
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(false)
            } else {
                orderRepository.observeIsAdmin(user.id).catch { emit(false) }
            }
        }
        .distinctUntilChanged()
}

class UpdateOrderStatusUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    fun execute(orderNumber: String, status: OrderStatus): Flow<NetworkResult<Unit?>> = flow {
        orderRepository.updateStatus(orderNumber, status).collect {
            emit(it)
        }
    }
}
