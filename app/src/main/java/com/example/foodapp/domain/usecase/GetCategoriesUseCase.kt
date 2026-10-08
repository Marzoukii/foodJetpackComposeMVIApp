package com.example.foodapp.domain.usecase

import com.example.foodapp.data.NetworkResult
import com.example.foodapp.data.repository.FoodRepository
import com.example.foodapp.domain.model.CategoriesDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    fun execute(): Flow<NetworkResult<CategoriesDataModel?>> = flow {
        foodRepository.getCategories().collect {
            emit(it)
        }
    }
}
