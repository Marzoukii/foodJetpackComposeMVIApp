package com.example.foodapp.ui.util

/**
 * TheMealDB renvoie les noms en anglais : on les traduit pour l'affichage.
 * Les appels API gardent toujours le nom anglais.
 */
private val categoryLabels = mapOf(
    "Beef" to "Bœuf",
    "Breakfast" to "Petit-déjeuner",
    "Chicken" to "Poulet",
    "Dessert" to "Dessert",
    "Goat" to "Chèvre",
    "Lamb" to "Agneau",
    "Miscellaneous" to "Divers",
    "Pasta" to "Pâtes",
    "Pork" to "Porc",
    "Seafood" to "Fruits de mer",
    "Side" to "Accompagnement",
    "Starter" to "Entrée",
    "Vegan" to "Vegan",
    "Vegetarian" to "Végétarien"
)

private val areaLabels = mapOf(
    "Algerian" to "Algérien",
    "American" to "Américain",
    "Argentinian" to "Argentin",
    "Australian" to "Australien",
    "British" to "Britannique",
    "Canadian" to "Canadien",
    "Chinese" to "Chinois",
    "Croatian" to "Croate",
    "Dutch" to "Néerlandais",
    "Egyptian" to "Égyptien",
    "Filipino" to "Philippin",
    "French" to "Français",
    "Greek" to "Grec",
    "India" to "Indien",
    "Indian" to "Indien",
    "Irish" to "Irlandais",
    "Italian" to "Italien",
    "Jamaican" to "Jamaïcain",
    "Japanese" to "Japonais",
    "Kenyan" to "Kényan",
    "Malaysian" to "Malaisien",
    "Mexican" to "Mexicain",
    "Moroccan" to "Marocain",
    "Norwegian" to "Norvégien",
    "Polish" to "Polonais",
    "Portuguese" to "Portugais",
    "Russian" to "Russe",
    "Saudi Arabian" to "Saoudien",
    "Slovakian" to "Slovaque",
    "Spanish" to "Espagnol",
    "Syrian" to "Syrien",
    "Thai" to "Thaïlandais",
    "Tunisian" to "Tunisien",
    "Turkish" to "Turc",
    "Ukrainian" to "Ukrainien",
    "Uruguayan" to "Uruguayen",
    "United Kingdom" to "Britannique",
    "United States" to "Américain",
    "Venezulan" to "Vénézuélien",
    "Vietnamese" to "Vietnamien"
)

fun categoryLabel(name: String?): String = name?.let { categoryLabels[it] ?: it }.orEmpty()

fun areaLabel(name: String?): String? =
    name?.takeIf { it.isNotBlank() && it != "Unknown" }?.let { areaLabels[it] ?: it }
