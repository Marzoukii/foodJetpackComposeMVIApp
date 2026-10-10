package com.example.foodapp.data.service

import com.example.foodapp.data.model.CategoriesDataJson
import com.example.foodapp.data.model.MealDetailsDataJson
import com.example.foodapp.data.model.MealsListDataJson
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FoodService {

    @GET("categories.php")
    suspend fun getCategories(): Response<CategoriesDataJson>

    @GET("filter.php")
    suspend fun getMealsByCategory(@Query("c") category: String): Response<MealsListDataJson>

    @GET("filter.php")
    suspend fun getMealsByIngredient(@Query("i") ingredient: String): Response<MealsListDataJson>

    @GET("lookup.php")
    suspend fun getMealDetails(@Query("i") mealId: String): Response<MealDetailsDataJson>

    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): Response<MealDetailsDataJson>

    @GET("random.php")
    suspend fun getRandomMeal(): Response<MealDetailsDataJson>
}