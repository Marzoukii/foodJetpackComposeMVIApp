package com.example.foodapp.domain.usecase

import com.example.foodapp.data.NetworkResult
import com.example.foodapp.data.repository.FoodRepository
import com.example.foodapp.domain.model.MealDetailsDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMealDetailsUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    fun execute(mealId: String): Flow<NetworkResult<MealDetailsDataModel?>> = flow {
        foodRepository.getMealDetails(mealId).collect {
            emit(it)
        }
    }
}
