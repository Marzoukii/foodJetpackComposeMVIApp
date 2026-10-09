package com.example.foodapp.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.ui.theme.PriceTextStyle
import com.example.foodapp.ui.theme.Spacing
import com.example.foodapp.ui.theme.placeholderColorFor

// ---------- Boutons ----------

/** Button · primary : 56 dp, forme pleine. */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        modifier = modifier.height(56.dp),
        contentPadding = PaddingValues(horizontal = Spacing.xxl)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold))
    }
}

/** Button · outlined. */
@Composable
fun OutlinedPillButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        shape = CircleShape,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
        modifier = modifier.height(56.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

/** Bouton rond blanc 48 dp (retour, panier…). */
@Composable
fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    bordered: Boolean = true,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = if (bordered) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null,
        modifier = modifier.size(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(22.dp))
        }
    }
}

/** Barre du haut : retour + titre + action optionnelle. */
@Composable
fun BackTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    action: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xl, vertical = Spacing.m),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        CircleIconButton(BoucheeIcons.Back, contentDescription = stringResource(R.string.common_back), onClick = onBack)
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        action()
    }
}

// ---------- Chips ----------

/** CategoryChip : sélectionné = fond onSurface. */
@Composable
fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) colors.onSurface else colors.surfaceContainer,
        contentColor = if (selected) colors.surfaceContainer else colors.onSurface,
        border = if (selected) null else BorderStroke(1.dp, colors.outlineVariant),
        modifier = modifier.height(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 18.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                )
            )
        }
    }
}

/** Petite étiquette arrondie (zone, catégorie, « Plat du jour »). */
@Composable
fun Tag(text: String, background: Color, modifier: Modifier = Modifier, contentColor: Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = contentColor,
        modifier = modifier
            .clip(CircleShape)
            .background(background)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

// ---------- Stepper ----------

enum class StepperSize(val button: Dp, val icon: Dp, val valueWidth: Dp) {
    Medium(44.dp, 20.dp, 28.dp),
    Small(34.dp, 16.dp, 24.dp)
}

/** QuantityStepper : Row + 2 boutons ronds sur fond surfaceContainerHigh. */
@Composable
fun QuantityStepper(
    quantity: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
    size: StepperSize = StepperSize.Medium,
    accentIncrement: Boolean = size == StepperSize.Small
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(colors.surfaceContainerHigh)
            .padding(if (size == StepperSize.Small) 3.dp else 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        StepperButton(BoucheeIcons.Minus, stringResource(R.string.common_remove), onDecrement, size, colors.surfaceContainer, colors.onSurface)
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.width(size.valueWidth)
        )
        StepperButton(
            BoucheeIcons.Plus, stringResource(R.string.common_add), onIncrement, size,
            if (accentIncrement) colors.primary else colors.surfaceContainer,
            if (accentIncrement) colors.onPrimary else colors.onSurface
        )
    }
}

@Composable
private fun StepperButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    size: StepperSize,
    background: Color,
    tint: Color
) {
    Surface(onClick = onClick, shape = CircleShape, color = background, modifier = Modifier.size(size.button)) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = description, tint = tint, modifier = Modifier.size(size.icon))
        }
    }
}

// ---------- Images et cartes ----------

/** Image d'un plat avec fond coloré pendant le chargement. */
@Composable
fun MealImage(url: String?, contentDescription: String?, modifier: Modifier = Modifier) {
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier.background(placeholderColorFor(url))
    )
}

/** Bouton carré rouge « + » des cartes plat. */
@Composable
fun AddToCartButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier.size(44.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(BoucheeIcons.Plus, contentDescription = stringResource(R.string.component_add_to_cart), modifier = Modifier.size(22.dp))
        }
    }
}

/** MealCard · layout = row. */
@Composable
fun MealRowCard(
    name: String,
    subtitle: String?,
    price: String,
    imageUrl: String?,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(onClick = onClick, modifier = modifier) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MealImage(
                url = imageUrl,
                contentDescription = name,
                modifier = Modifier
                    .size(84.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(name, style = MaterialTheme.typography.titleSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (!subtitle.isNullOrBlank()) {
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(price, style = PriceTextStyle)
            }
            AddToCartButton(onClick = onAddToCart)
        }
    }
}

/** MealCard · layout = grid. */
@Composable
fun MealGridCard(
    name: String,
    subtitle: String?,
    price: String,
    imageUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(onClick = onClick, modifier = modifier) {
        Column {
            MealImage(
                url = imageUrl,
                contentDescription = name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp)
            )
            Column(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 10.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(2.dp))
                Text(price, style = PriceTextStyle.copy(fontSize = PriceTextStyle.fontSize * 0.94f))
            }
        }
    }
}

/** Carte à plat (niveau 0) : fond blanc + contour outlineVariant, rayon 20. */
@Composable
fun OutlinedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    content: @Composable () -> Unit
) {
    if (onClick != null) {
        Surface(
            onClick = onClick,
            shape = shape,
            color = MaterialTheme.colorScheme.surfaceContainer,
            border = border,
            modifier = modifier,
            content = content
        )
    } else {
        Surface(
            shape = shape,
            color = MaterialTheme.colorScheme.surfaceContainer,
            border = border,
            modifier = modifier,
            content = content
        )
    }
}

// ---------- Recherche ----------

/**
 * SearchField. Avec [onClick], le champ est un raccourci vers l'écran de recherche
 * (non éditable) ; sinon il est éditable.
 */
@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(R.string.common_search_meal),
    textFieldModifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme
    val fieldModifier = modifier
        .fillMaxWidth()
        .height(52.dp)
        .clip(MaterialTheme.shapes.medium)
        .background(colors.surfaceContainerHigh)
        .let { if (onClick != null) it.clickable(role = Role.Button, onClick = onClick) else it }
        .padding(horizontal = Spacing.l)

    Row(
        modifier = fieldModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        Icon(BoucheeIcons.Search, contentDescription = null, tint = colors.onSurfaceVariant, modifier = Modifier.size(22.dp))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (value.isEmpty()) {
                Text(placeholder, style = MaterialTheme.typography.bodyMedium.copy(fontSize = PriceTextStyle.fontSize * 0.88f), color = colors.onSurfaceVariant)
            }
            if (onClick == null) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = PriceTextStyle.fontSize * 0.88f, color = colors.onSurface),
                    cursorBrush = SolidColor(colors.primary),
                    modifier = textFieldModifier.fillMaxWidth()
                )
            }
        }
    }
}

// ---------- Récapitulatif et barre d'action ----------

data class PriceLine(val label: String, val value: String)

/** PriceSummary : lignes secondaires + total. */
@Composable
fun PriceSummary(lines: List<PriceLine>, totalLabel: String, total: String, modifier: Modifier = Modifier) {
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            lines.forEach { line ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(line.label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                    Text(line.value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(totalLabel, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Text(total, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

/** StickyCTA : barre blanche fixe en bas, ombre niveau 2. */
@Composable
fun StickyBottomBar(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(16.dp, ambientColor = Color(0x141B1A17), spotColor = Color(0x141B1A17))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .navigationBarsPadding()
            .padding(start = Spacing.xl, end = Spacing.xl, top = Spacing.l, bottom = Spacing.l),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
        content = content
    )
}

// ---------- Navigation du bas ----------

enum class BottomTab(@StringRes val labelRes: Int, val icon: ImageVector) {
    Home(R.string.tab_home, BoucheeIcons.Home),
    Menu(R.string.tab_menu, BoucheeIcons.Menu),
    Cart(R.string.tab_cart, BoucheeIcons.Bag)
}

/** BottomNav : NavigationBar, indicateur primaryContainer, badge sur Panier. */
@Composable
fun BoucheeBottomBar(selected: BottomTab, cartCount: Int, onTabSelected: (BottomTab) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.surfaceContainerHigh)
        )
        NavigationBar(containerColor = colors.surfaceContainer, tonalElevation = 0.dp) {
            BottomTab.entries.forEach { tab ->
                NavigationBarItem(
                    selected = tab == selected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        if (tab == BottomTab.Cart && cartCount > 0) {
                            BadgedBox(badge = { Badge { Text(cartCount.toString()) } }) {
                                Icon(tab.icon, contentDescription = null)
                            }
                        } else {
                            Icon(tab.icon, contentDescription = null)
                        }
                    },
                    label = { Text(stringResource(tab.labelRes), style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.primary,
                        selectedTextColor = colors.primary,
                        indicatorColor = colors.primaryContainer,
                        unselectedIconColor = colors.onSurfaceVariant,
                        unselectedTextColor = colors.onSurfaceVariant
                    )
                )
            }
        }
    }
}

/** Bouton du panier avec badge (en-tête de l'accueil). */
@Composable
fun CartIconButton(count: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        CircleIconButton(BoucheeIcons.Bag, contentDescription = stringResource(R.string.component_cart_with_count, pluralStringResource(R.plurals.common_articles, count, count)), onClick = onClick)
        if (count > 0) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    count.toString(),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.labelMedium.fontSize * 0.92f),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
