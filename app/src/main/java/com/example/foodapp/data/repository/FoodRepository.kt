package com.example.foodapp.data.repository

import com.example.foodapp.data.NetworkResult
import com.example.foodapp.data.service.FoodService
import com.example.foodapp.domain.mapper.FoodMapper
import com.example.foodapp.domain.model.CategoriesDataModel
import com.example.foodapp.domain.model.MealDetailsDataModel
import com.example.foodapp.domain.model.MealsListDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FoodRepository @Inject constructor(
    private val foodService: FoodService,
    private val foodMapper: FoodMapper
) {

    fun getCategories(): Flow<NetworkResult<CategoriesDataModel?>> = flow {
        try {
            val response = foodService.getCategories()
            if (response.isSuccessful) {
                emit(NetworkResult.Success(foodMapper.mapCategories(response.body())))
            } else {
                emit(NetworkResult.Error(Exception("Error: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e))
        }
    }

    fun getMealsByCategory(category: String): Flow<NetworkResult<MealsListDataModel?>> = flow {
        try {
            val response = foodService.getMealsByCategory(category)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(foodMapper.mapMealsList(response.body())))
            } else {
                emit(NetworkResult.Error(Exception("Error: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e))
        }
    }

    fun getMealDetails(mealId: String): Flow<NetworkResult<MealDetailsDataModel?>> = flow {
        try {
            val response = foodService.getMealDetails(mealId)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(foodMapper.mapMealDetails(response.body())))
            } else {
                emit(NetworkResult.Error(Exception("Error: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e))
        }
    }

    fun searchMeals(query: String): Flow<NetworkResult<MealDetailsDataModel?>> = flow {
        try {
            val response = foodService.searchMeals(query)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(foodMapper.mapMealDetails(response.body())))
            } else {
                emit(NetworkResult.Error(Exception("Error: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e))
        }
    }

    fun getRandomMeal(): Flow<NetworkResult<MealDetailsDataModel?>> = flow {
        try {
            val response = foodService.getRandomMeal()
            if (response.isSuccessful) {
                emit(NetworkResult.Success(foodMapper.mapMealDetails(response.body())))
            } else {
                emit(NetworkResult.Error(Exception("Error: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e))
        }
    }
}
