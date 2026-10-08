package com.example.foodapp.domain.model

data class MealDetailsDataModel(
    val meals: List<MealModel>?
)

data class MealModel(
    val id: String?,
    val name: String?,
    val alternateName: String?,
    val category: String?,
    val area: String?,
    val country: String?,
    val instructions: String?,
    val thumbnail: String?,
    val tags: String?,
    val youtubeUrl: String?,

    val ingredient1: String?,
    val ingredient2: String?,
    val ingredient3: String?,
    val ingredient4: String?,
    val ingredient5: String?,
    val ingredient6: String?,
    val ingredient7: String?,
    val ingredient8: String?,
    val ingredient9: String?,
    val ingredient10: String?,
    val ingredient11: String?,
    val ingredient12: String?,
    val ingredient13: String?,
    val ingredient14: String?,
    val ingredient15: String?,
    val ingredient16: String?,
    val ingredient17: String?,
    val ingredient18: String?,
    val ingredient19: String?,
    val ingredient20: String?,

    val measure1: String?,
    val measure2: String?,
    val measure3: String?,
    val measure4: String?,
    val measure5: String?,
    val measure6: String?,
    val measure7: String?,
    val measure8: String?,
    val measure9: String?,
    val measure10: String?,
    val measure11: String?,
    val measure12: String?,
    val measure13: String?,
    val measure14: String?,
    val measure15: String?,
    val measure16: String?,
    val measure17: String?,
    val measure18: String?,
    val measure19: String?,
    val measure20: String?,

    val sourceUrl: String?,
    val imageSource: String?,
    val creativeCommonsConfirmed: String?,
    val dateModified: String?
) {
    fun ingredients(): List<Pair<String, String>> = listOf(
        ingredient1 to measure1, ingredient2 to measure2, ingredient3 to measure3,
        ingredient4 to measure4, ingredient5 to measure5, ingredient6 to measure6,
        ingredient7 to measure7, ingredient8 to measure8, ingredient9 to measure9,
        ingredient10 to measure10, ingredient11 to measure11, ingredient12 to measure12,
        ingredient13 to measure13, ingredient14 to measure14, ingredient15 to measure15,
        ingredient16 to measure16, ingredient17 to measure17, ingredient18 to measure18,
        ingredient19 to measure19, ingredient20 to measure20
    ).filter { !it.first.isNullOrBlank() }
        .map { it.first!!.trim() to it.second.orEmpty().trim() }

    fun cleanInstructions(): String = instructions.orEmpty().replace("\r\n", "\n")
}