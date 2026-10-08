package com.example.foodapp.domain.model

data class CategoriesDataModel(
    val categories: List<CategoryModel>?
)

data class CategoryModel(
    val id: String?,
    val name: String?,
    val thumbnail: String?,
    val description: String?
)
