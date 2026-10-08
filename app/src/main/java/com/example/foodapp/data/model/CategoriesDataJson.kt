package com.example.foodapp.data.model

import com.google.gson.annotations.SerializedName

data class CategoriesDataJson(
    @SerializedName("categories")
    val categories: List<CategoryJson>?
)

data class CategoryJson(
    @SerializedName("idCategory")
    val id: String?,
    @SerializedName("strCategory")
    val name: String?,
    @SerializedName("strCategoryThumb")
    val thumbnail: String?,
    @SerializedName("strCategoryDescription")
    val description: String?
)