package com.example.foodapp.domain.model

data class MealsListDataModel(
    val meals: List<MealItemModel>?
)

data class MealItemModel(
    val id: String?,
    val name: String?,
    val thumbnail: String?
)