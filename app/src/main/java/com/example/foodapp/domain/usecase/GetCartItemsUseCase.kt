package com.example.foodapp.domain.usecase

import com.example.foodapp.data.repository.CartRepository
import com.example.foodapp.domain.model.CartItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCartItemsUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    fun execute(): Flow<List<CartItemModel>> = flow {
        cartRepository.getCartItems().collect {
            emit(it)
        }
    }
}
