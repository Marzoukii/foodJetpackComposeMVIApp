package com.example.foodapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Une ligne du panier = un plat + les ingrédients retirés.
 * Le même plat avec des ingrédients retirés différents donne deux lignes distinctes.
 */
@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val lineId: String,
    val mealId: String,
    val name: String,
    val thumbnail: String?,
    val unitPriceCents: Int,
    val quantity: Int,
    /** Ingrédients retirés, séparés par [SEPARATOR] (vide si aucun). */
    val removedIngredients: String,
    val addedAt: Long
) {
    companion object {
        const val SEPARATOR = "|"

        fun lineIdFor(mealId: String, removedIngredients: Set<String>): String =
            if (removedIngredients.isEmpty()) mealId
            else mealId + SEPARATOR + removedIngredients.sorted().joinToString(SEPARATOR)
    }
}
