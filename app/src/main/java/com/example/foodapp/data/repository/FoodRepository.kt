package com.example.foodapp.data.repository

import com.example.foodapp.data.NetworkResult
import com.example.foodapp.data.filter.PorkFilter
import com.example.foodapp.data.model.MealDetailsDataJson
import com.example.foodapp.data.service.FoodService
import com.example.foodapp.domain.mapper.FoodMapper
import com.example.foodapp.domain.model.CategoriesDataModel
import com.example.foodapp.domain.model.MealDetailsDataModel
import com.example.foodapp.domain.model.MealsListDataModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
                val body = response.body()?.let { json ->
                    json.copy(categories = json.categories?.filterNot { PorkFilter.isPorkCategory(it.name) })
                }
                emit(NetworkResult.Success(foodMapper.mapCategories(body)))
            } else {
                emit(NetworkResult.Error(Exception("Error: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e))
        }
    }

    fun getMealsByCategory(category: String): Flow<NetworkResult<MealsListDataModel?>> = flow {
        try {
            if (PorkFilter.isPorkCategory(category)) {
                emit(NetworkResult.Success(foodMapper.mapMealsList(null)))
                return@flow
            }
            val response = foodService.getMealsByCategory(category)
            if (response.isSuccessful) {
                val porkIds = porkMealIds()
                val body = response.body()?.let { json ->
                    json.copy(meals = json.meals?.filterNot { it.id in porkIds || PorkFilter.isPorkText(it.name) })
                }
                emit(NetworkResult.Success(foodMapper.mapMealsList(body)))
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
                emit(NetworkResult.Success(foodMapper.mapMealDetails(withoutPork(response.body()))))
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
                emit(NetworkResult.Success(foodMapper.mapMealDetails(withoutPork(response.body()))))
            } else {
                emit(NetworkResult.Error(Exception("Error: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e))
        }
    }

    fun getRandomMeal(): Flow<NetworkResult<MealDetailsDataModel?>> = flow {
        try {
            var response = foodService.getRandomMeal()
            var attempts = 1
            // Plat au hasard : on en retire un autre tant qu'il contient du porc.
            while (response.isSuccessful && withoutPork(response.body())?.meals == null && attempts < MAX_RANDOM_ATTEMPTS) {
                response = foodService.getRandomMeal()
                attempts++
            }
            if (response.isSuccessful) {
                emit(NetworkResult.Success(foodMapper.mapMealDetails(withoutPork(response.body()))))
            } else {
                emit(NetworkResult.Error(Exception("Error: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e))
        }
    }

    private fun withoutPork(json: MealDetailsDataJson?): MealDetailsDataJson? =
        json?.copy(meals = json.meals?.filterNot { PorkFilter.isPorkMeal(it) }?.ifEmpty { null })

    /** Identifiants des plats contenant un ingrédient à base de porc (mis en cache une fois la liste complète). */
    private suspend fun porkMealIds(): Set<String> {
        porkMealIdsCache?.let { return it }
        var complete = true
        val ids = coroutineScope {
            PorkFilter.PORK_INGREDIENTS.map { ingredient ->
                async {
                    try {
                        val response = foodService.getMealsByIngredient(ingredient)
                        if (!response.isSuccessful) complete = false
                        response.body()?.meals.orEmpty().mapNotNull { it.id }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        complete = false
                        emptyList()
                    }
                }
            }.awaitAll().flatten().toSet()
        }
        if (complete) porkMealIdsCache = ids
        return ids
    }

    private companion object {
        const val MAX_RANDOM_ATTEMPTS = 10

        @Volatile
        var porkMealIdsCache: Set<String>? = null
    }
}
