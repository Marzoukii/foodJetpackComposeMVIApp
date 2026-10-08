package com.example.foodapp.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/** Icônes du design (contour 2 dp, viewport 24). La couleur vient du `tint` de Icon. */
object BoucheeIcons {
    val Back = strokeIcon("Back", "M15 18l-6-6 6-6")
    val Search = strokeIcon("Search", "M4 11a7 7 0 1 0 14 0a7 7 0 1 0 -14 0", "M20 20l-4-4")
    val Bag = strokeIcon("Bag", "M6 7h12l-1 13H7L6 7z", "M9 7a3 3 0 0 1 6 0")
    val Home = strokeIcon("Home", "M4 11l8-7 8 7v9h-5v-6H9v6H4z")
    val Menu = strokeIcon("Menu", "M5 6h14", "M5 12h14", "M5 18h14")
    val Plus = strokeIcon("Plus", "M12 5v14", "M5 12h14")
    val Minus = strokeIcon("Minus", "M5 12h14")
    val Pin = strokeIcon("Pin", "M12 21s-7-6-7-11a7 7 0 0 1 14 0c0 5-7 11-7 11z", "M9.5 10a2.5 2.5 0 1 0 5 0a2.5 2.5 0 1 0 -5 0")
    val Card = strokeIcon("Card", "M5 6h14a2 2 0 0 1 2 2v8a2 2 0 0 1 -2 2H5a2 2 0 0 1 -2 -2V8a2 2 0 0 1 2 -2z", "M3 10h18")
    val Cash = strokeIcon("Cash", "M4 7h16v10H4z", "M9.5 12a2.5 2.5 0 1 0 5 0a2.5 2.5 0 1 0 -5 0")
    val Clock = strokeIcon("Clock", "M3 12a9 9 0 1 0 18 0a9 9 0 1 0 -18 0", "M12 7v5l3 2")
    val Check = strokeIcon("Check", "M5 13l4 4L19 7")

    private fun strokeIcon(name: String, vararg paths: String): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            paths.forEach { d ->
                addPath(
                    pathData = addPathNodes(d),
                    fill = null,
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                )
            }
        }.build()
}
