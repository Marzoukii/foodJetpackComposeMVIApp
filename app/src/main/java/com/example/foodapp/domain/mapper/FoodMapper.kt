package com.example.foodapp.domain.mapper

import com.example.foodapp.data.model.CategoriesDataJson
import com.example.foodapp.data.model.CategoryJson
import com.example.foodapp.data.model.MealDetailsDataJson
import com.example.foodapp.data.model.MealItemJson
import com.example.foodapp.data.model.MealJson
import com.example.foodapp.data.model.MealsListDataJson
import com.example.foodapp.domain.model.CategoriesDataModel
import com.example.foodapp.domain.model.CategoryModel
import com.example.foodapp.domain.model.MealDetailsDataModel
import com.example.foodapp.domain.model.MealItemModel
import com.example.foodapp.domain.model.MealModel
import com.example.foodapp.domain.model.MealsListDataModel
import javax.inject.Inject

class FoodMapper @Inject constructor() {

    fun mapCategories(json: CategoriesDataJson?): CategoriesDataModel? {
        if (json == null) return null
        return CategoriesDataModel(
            categories = json.categories?.map { mapCategory(it) }
        )
    }

    private fun mapCategory(json: CategoryJson): CategoryModel {
        return CategoryModel(
            id = json.id,
            name = json.name,
            thumbnail = json.thumbnail,
            description = json.description
        )
    }

    fun mapMealsList(json: MealsListDataJson?): MealsListDataModel? {
        if (json == null) return null
        return MealsListDataModel(
            meals = json.meals?.map { mapMealItem(it) }
        )
    }

    private fun mapMealItem(json: MealItemJson): MealItemModel {
        return MealItemModel(
            id = json.id,
            name = json.name,
            thumbnail = json.thumbnail
        )
    }

    fun mapMealDetails(json: MealDetailsDataJson?): MealDetailsDataModel? {
        if (json == null) return null
        return MealDetailsDataModel(
            meals = json.meals?.map { mapMeal(it) }
        )
    }

    private fun mapMeal(json: MealJson): MealModel {
        return MealModel(
            id = json.id,
            name = json.name,
            alternateName = json.alternateName,
            category = json.category,
            area = json.area,
            country = json.country,
            instructions = json.instructions,
            thumbnail = json.thumbnail,
            tags = json.tags,
            youtubeUrl = json.youtubeUrl,
            ingredient1 = json.ingredient1,
            ingredient2 = json.ingredient2,
            ingredient3 = json.ingredient3,
            ingredient4 = json.ingredient4,
            ingredient5 = json.ingredient5,
            ingredient6 = json.ingredient6,
            ingredient7 = json.ingredient7,
            ingredient8 = json.ingredient8,
            ingredient9 = json.ingredient9,
            ingredient10 = json.ingredient10,
            ingredient11 = json.ingredient11,
            ingredient12 = json.ingredient12,
            ingredient13 = json.ingredient13,
            ingredient14 = json.ingredient14,
            ingredient15 = json.ingredient15,
            ingredient16 = json.ingredient16,
            ingredient17 = json.ingredient17,
            ingredient18 = json.ingredient18,
            ingredient19 = json.ingredient19,
            ingredient20 = json.ingredient20,
            measure1 = json.measure1,
            measure2 = json.measure2,
            measure3 = json.measure3,
            measure4 = json.measure4,
            measure5 = json.measure5,
            measure6 = json.measure6,
            measure7 = json.measure7,
            measure8 = json.measure8,
            measure9 = json.measure9,
            measure10 = json.measure10,
            measure11 = json.measure11,
            measure12 = json.measure12,
            measure13 = json.measure13,
            measure14 = json.measure14,
            measure15 = json.measure15,
            measure16 = json.measure16,
            measure17 = json.measure17,
            measure18 = json.measure18,
            measure19 = json.measure19,
            measure20 = json.measure20,
            sourceUrl = json.sourceUrl,
            imageSource = json.imageSource,
            creativeCommonsConfirmed = json.creativeCommonsConfirmed,
            dateModified = json.dateModified
        )
    }
}
