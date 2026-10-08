package com.example.foodapp.domain.usecase

import com.example.foodapp.data.NetworkResult
import com.example.foodapp.data.repository.FoodRepository
import com.example.foodapp.domain.model.MealsListDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMealsByCategoryUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    fun execute(category: String): Flow<NetworkResult<MealsListDataModel?>> = flow {
        foodRepository.getMealsByCategory(category).collect {
            emit(it)
        }
    }
}
