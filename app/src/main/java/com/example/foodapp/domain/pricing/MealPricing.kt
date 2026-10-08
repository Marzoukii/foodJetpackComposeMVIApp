package com.example.foodapp.domain.pricing

/**
 * TheMealDB ne fournit pas de prix : on en calcule un, stable pour chaque plat,
 * à partir de son idMeal (entre 8,90 € et 17,80 €).
 */
object MealPricing {

    const val DELIVERY_FEE_CENTS = 299

    fun priceCentsFor(mealId: String?): Int {
        val seed = mealId?.toLongOrNull() ?: (mealId?.hashCode()?.toLong() ?: 0L)
        return 890 + ((seed and Long.MAX_VALUE) % 90).toInt() * 10
    }
}
