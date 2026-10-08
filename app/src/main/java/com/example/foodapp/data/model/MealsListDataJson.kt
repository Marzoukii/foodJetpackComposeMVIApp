package com.example.foodapp.data.model

import com.google.gson.annotations.SerializedName

data class MealsListDataJson(
    @SerializedName("meals")
    val meals: List<MealItemJson>?
)

data class MealItemJson(
    @SerializedName("idMeal")
    val id: String?,
    @SerializedName("strMeal")
    val name: String?,
    @SerializedName("strMealThumb")
    val thumbnail: String?
)