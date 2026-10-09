package com.example.foodapp.ui.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * Texte affichable produit hors de l'UI (ViewModel) : une ressource + ses arguments,
 * résolue au moment de l'affichage. Les ViewModels n'ont ainsi pas besoin de Context.
 */
sealed interface UiText {
    /** Texte déjà prêt, venu de l'extérieur (message d'une exception...). */
    data class Dynamic(val value: String) : UiText

    class Resource(@StringRes val id: Int, vararg val args: Any) : UiText {
        override fun equals(other: Any?): Boolean =
            other is Resource && other.id == id && other.args.contentEquals(args)

        override fun hashCode(): Int = 31 * id + args.contentHashCode()
    }

    @Composable
    fun asString(): String = when (this) {
        is Dynamic -> value
        is Resource -> stringResource(id, *resolveArgs { it.asString() })
    }

    fun asString(context: Context): String = when (this) {
        is Dynamic -> value
        is Resource -> context.getString(id, *resolveArgs { it.asString(context) })
    }
}

/** Un argument peut lui-même être un UiText (ex. libellé de statut dans un message). */
private inline fun UiText.Resource.resolveArgs(resolve: (UiText) -> String): Array<Any> =
    args.map { if (it is UiText) resolve(it) else it }.toTypedArray()

fun uiText(@StringRes id: Int, vararg args: Any): UiText = UiText.Resource(id, *args)

/** Message d'une exception, ou [fallback] s'il est vide. */
fun Throwable.toUiText(@StringRes fallback: Int): UiText =
    message?.takeIf { it.isNotBlank() }?.let { UiText.Dynamic(it) } ?: UiText.Resource(fallback)
