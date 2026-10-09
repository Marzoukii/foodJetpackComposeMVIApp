package com.example.foodapp.ui.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.foodapp.R

/**
 * TheMealDB renvoie les noms en anglais : on les traduit pour l'affichage.
 * Les appels API gardent toujours le nom anglais.
 */
private val categoryLabels = mapOf(
    "Beef" to R.string.category_beef,
    "Breakfast" to R.string.category_breakfast,
    "Chicken" to R.string.category_chicken,
    "Dessert" to R.string.category_dessert,
    "Goat" to R.string.category_goat,
    "Lamb" to R.string.category_lamb,
    "Miscellaneous" to R.string.category_miscellaneous,
    "Pasta" to R.string.category_pasta,
    "Pork" to R.string.category_pork,
    "Seafood" to R.string.category_seafood,
    "Side" to R.string.category_side,
    "Starter" to R.string.category_starter,
    "Vegan" to R.string.category_vegan,
    "Vegetarian" to R.string.category_vegetarian
)

private val areaLabels = mapOf(
    "Algerian" to R.string.area_algerian,
    "American" to R.string.area_american,
    "Argentinian" to R.string.area_argentinian,
    "Australian" to R.string.area_australian,
    "British" to R.string.area_british,
    "Canadian" to R.string.area_canadian,
    "Chinese" to R.string.area_chinese,
    "Croatian" to R.string.area_croatian,
    "Dutch" to R.string.area_dutch,
    "Egyptian" to R.string.area_egyptian,
    "Filipino" to R.string.area_filipino,
    "French" to R.string.area_french,
    "Greek" to R.string.area_greek,
    "India" to R.string.area_indian,
    "Indian" to R.string.area_indian,
    "Irish" to R.string.area_irish,
    "Italian" to R.string.area_italian,
    "Jamaican" to R.string.area_jamaican,
    "Japanese" to R.string.area_japanese,
    "Kenyan" to R.string.area_kenyan,
    "Malaysian" to R.string.area_malaysian,
    "Mexican" to R.string.area_mexican,
    "Moroccan" to R.string.area_moroccan,
    "Norwegian" to R.string.area_norwegian,
    "Polish" to R.string.area_polish,
    "Portuguese" to R.string.area_portuguese,
    "Russian" to R.string.area_russian,
    "Saudi Arabian" to R.string.area_saudi_arabian,
    "Slovakian" to R.string.area_slovakian,
    "Spanish" to R.string.area_spanish,
    "Syrian" to R.string.area_syrian,
    "Thai" to R.string.area_thai,
    "Tunisian" to R.string.area_tunisian,
    "Turkish" to R.string.area_turkish,
    "Ukrainian" to R.string.area_ukrainian,
    "Uruguayan" to R.string.area_uruguayan,
    "United Kingdom" to R.string.area_british,
    "United States" to R.string.area_american,
    "Venezulan" to R.string.area_venezuelan,
    "Vietnamese" to R.string.area_vietnamese
)

@Composable
fun categoryLabel(name: String?): String = name?.let { translate(it, categoryLabels[it]) }.orEmpty()

@Composable
fun areaLabel(name: String?): String? =
    name?.takeIf { it.isNotBlank() && it != "Unknown" }?.let { translate(it, areaLabels[it]) }

/** Nom inconnu de la table de traduction : on affiche le nom anglais tel quel. */
@Composable
private fun translate(name: String, @StringRes id: Int?): String = id?.let { stringResource(it) } ?: name

/** « Bœuf · Américain » : éléments non vides séparés par un point médian. */
@Composable
fun joinDetails(vararg parts: String?): String =
    parts.filterNot { it.isNullOrBlank() }.joinToString(stringResource(R.string.common_list_separator))

/** « Sans Harissa, Sans Oignon » */
@Composable
fun withoutIngredients(ingredients: List<String>): String {
    val separator = stringResource(R.string.common_ingredients_separator)
    return ingredients.map { stringResource(R.string.common_without_ingredient, it) }.joinToString(separator)
}
