package com.example.foodapp.data.filter

import com.example.foodapp.data.model.MealJson

/**
 * L'application ne doit afficher aucun plat contenant du porc.
 * TheMealDB a une catégorie « Pork », mais d'autres catégories utilisent aussi
 * du porc (bacon, jambon, chorizo…) : on les repère par nom et par ingrédient.
 */
object PorkFilter {

    const val PORK_CATEGORY = "Pork"

    /** Ingrédients TheMealDB à base de porc, utilisés avec filter.php?i= pour exclure les plats des listes. */
    val PORK_INGREDIENTS = listOf(
        "Pork", "Pork Chops", "Minced Pork", "Pork Belly", "Pork Shoulder", "Pork Ribs",
        "Bacon", "Streaky Bacon", "Smoked Bacon", "Bacon Lardons", "Lardons",
        "Ham", "Parma Ham", "Gammon", "Pancetta", "Prosciutto", "Chorizo",
        "Salami", "Pepperoni", "Sausages", "Italian Fennel Sausages", "Black Pudding",
        "Lard", "Gelatine"
    )

    private val porkWords = Regex(
        "\\b(pork|bacon|ham|gammon|pancetta|prosciutto|chorizo|salami|pepperoni|sausages?|" +
            "chipolatas?|lard|lardons?|guanciale|speck|mortadella|black pudding|gelatine?)\\b",
        RegexOption.IGNORE_CASE
    )

    fun isPorkText(text: String?): Boolean = !text.isNullOrBlank() && porkWords.containsMatchIn(text)

    fun isPorkCategory(category: String?): Boolean = category.equals(PORK_CATEGORY, ignoreCase = true)

    fun isPorkMeal(meal: MealJson): Boolean =
        isPorkCategory(meal.category) || isPorkText(meal.name) || meal.ingredients().any { isPorkText(it.first) }
}
