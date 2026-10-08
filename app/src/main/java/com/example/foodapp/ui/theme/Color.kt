package com.example.foodapp.ui.theme

import androidx.compose.ui.graphics.Color

// Jetons du design system « Bouchée » (rôles du ColorScheme Material 3)
val Primary = Color(0xFFD9381E)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFFFFE1D9)
val OnPrimaryContainer = Color(0xFF5C1206)
val Secondary = Color(0xFFF2B705)
val OnSecondary = Color(0xFF1B1A17)
val SecondaryContainer = Color(0xFFFFF1C2)
val Surface = Color(0xFFF6F6F3)
val SurfaceContainer = Color(0xFFFFFFFF)
val SurfaceContainerHigh = Color(0xFFEDEDE8)
val OnSurface = Color(0xFF1B1A17)
val OnSurfaceVariant = Color(0xFF5E5B54)
val OutlineVariant = Color(0xFFD6D4CE)
val Error = Color(0xFFB3261E)

// Couleurs hors ColorScheme utilisées par les écrans
val Success = Color(0xFF1F7A4D)
val SuccessContainer = Color(0xFFD7EBDF)

/** Fonds de remplacement des images, pendant le chargement. */
val PlaceholderColors = listOf(
    Color(0xFFF3C9B8),
    Color(0xFFCFE3D2),
    Color(0xFFF7DE9A),
    Color(0xFFE7D3C2),
    Color(0xFFFFE1D9)
)

fun placeholderColorFor(key: String?): Color =
    PlaceholderColors[((key?.hashCode() ?: 0) and Int.MAX_VALUE) % PlaceholderColors.size]
