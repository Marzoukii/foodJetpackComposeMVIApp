package com.example.foodapp.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.LocalContentColor
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodapp.R
import com.example.foodapp.domain.model.CategoryModel
import com.example.foodapp.domain.model.MealModel
import com.example.foodapp.domain.model.OrderType
import com.example.foodapp.domain.model.UserModel
import com.example.foodapp.domain.pricing.MealPricing
import com.example.foodapp.ui.components.BottomTab
import com.example.foodapp.ui.components.BoucheeBottomBar
import com.example.foodapp.ui.components.BoucheeIcons
import com.example.foodapp.ui.components.CartIconButton
import com.example.foodapp.ui.components.CircleIconButton
import com.example.foodapp.ui.components.ErrorView
import com.example.foodapp.ui.components.LoadingView
import com.example.foodapp.ui.components.MealGridCard
import com.example.foodapp.ui.components.MealImage
import com.example.foodapp.ui.components.OutlinedPillButton
import com.example.foodapp.ui.components.SearchField
import com.example.foodapp.ui.components.Tag
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.ui.theme.Spacing
import com.example.foodapp.ui.util.areaLabel
import com.example.foodapp.ui.util.categoryLabel
import com.example.foodapp.ui.util.formatPrice
import com.example.foodapp.ui.util.joinDetails

@Composable
fun HomeRoute(
    onNavigateToOrderMode: (clearBackStack: Boolean) -> Unit,
    onNavigateToMenu: (String?) -> Unit,
    onNavigateToMealDetails: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToAdminOrders: () -> Unit,
    onTabSelected: (BottomTab) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToMenu -> onNavigateToMenu(effect.categoryName)
                is HomeEffect.NavigateToMealDetails -> onNavigateToMealDetails(effect.mealId)
                HomeEffect.NavigateToSearch -> onNavigateToSearch()
                HomeEffect.NavigateToCart -> onNavigateToCart()
                is HomeEffect.NavigateToOrderMode -> onNavigateToOrderMode(effect.clearBackStack)
                HomeEffect.NavigateToAdminOrders -> onNavigateToAdminOrders()
            }
        }
    }

    HomeScreen(state = state, onIntent = viewModel::onIntent, onTabSelected = onTabSelected)
}

@Composable
fun HomeScreen(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    onTabSelected: (BottomTab) -> Unit
) {
    Scaffold(
        bottomBar = { BoucheeBottomBar(BottomTab.Home, state.cartCount, onTabSelected) }
    ) { padding ->
        when {
            state.isLoading && state.categories.isEmpty() -> LoadingView(Modifier.padding(padding))
            state.error != null && state.categories.isEmpty() -> ErrorView(
                message = state.error.asString(),
                onRetry = { onIntent(HomeIntent.Load) },
                modifier = Modifier.padding(padding)
            )
            else -> HomeContent(state, onIntent, Modifier.padding(padding))
        }
    }

    if (state.isAccountDialogVisible) {
        AccountDialog(
            user = state.user,
            isAdmin = state.isAdmin,
            onAdminOrders = { onIntent(HomeIntent.AdminOrdersClicked) },
            onSignOut = { onIntent(HomeIntent.SignOutClicked) },
            onDismiss = { onIntent(HomeIntent.DismissAccountDialog) }
        )
    }
}

@Composable
private fun HomeContent(state: HomeState, onIntent: (HomeIntent) -> Unit, modifier: Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = Spacing.xl, end = Spacing.xl, top = Spacing.l, bottom = Spacing.xxl),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxl)
    ) {
        item {
            HomeHeader(
                orderType = state.orderType,
                address = state.deliveryAddress,
                tableNumber = state.tableNumber,
                onModeClick = { onIntent(HomeIntent.ChangeOrderModeClicked) },
                cartCount = state.cartCount,
                onAccountClick = { onIntent(HomeIntent.AccountClicked) },
                onCartClick = { onIntent(HomeIntent.CartClicked) }
            )
        }

        item {
            Text(stringResource(R.string.home_title), style = MaterialTheme.typography.headlineMedium.copy(fontSize = MaterialTheme.typography.headlineMedium.fontSize * 1.07f))
        }

        item { SearchField(value = "", onValueChange = {}, onClick = { onIntent(HomeIntent.SearchClicked) }) }

        state.mealOfTheDay?.let { meal ->
            item { MealOfTheDayCard(meal) { meal.id?.let { onIntent(HomeIntent.MealClicked(it)) } } }
        }

        item {
            CategoriesSection(
                categories = state.categories,
                highlighted = state.selectedCategory,
                onCategoryClick = { onIntent(HomeIntent.CategoryClicked(it)) },
                onSeeAll = { onIntent(HomeIntent.SeeAllCategoriesClicked) }
            )
        }

        if (state.popularMeals.isNotEmpty()) {
            item { Text(stringResource(R.string.home_popular), style = MaterialTheme.typography.titleMedium) }
            // Grille de 2 colonnes dans la LazyColumn
            items(state.popularMeals.chunked(2)) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.m)) {
                    row.forEach { meal ->
                        MealGridCard(
                            name = meal.name.orEmpty(),
                            subtitle = categoryLabel(state.popularCategory),
                            price = formatPrice(MealPricing.priceCentsFor(meal.id)),
                            imageUrl = meal.thumbnail,
                            onClick = { meal.id?.let { onIntent(HomeIntent.MealClicked(it)) } },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    orderType: OrderType,
    address: String,
    tableNumber: Int?,
    onModeClick: () -> Unit,
    cartCount: Int,
    onAccountClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val isDineIn = orderType == OrderType.DINE_IN
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Toucher le mode ramène à l'écran de démarrage pour en changer.
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onModeClick),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                stringResource(if (isDineIn) R.string.home_dine_in_change else R.string.home_delivery_change),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                    if (isDineIn) BoucheeIcons.Table else BoucheeIcons.Pin,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (isDineIn) {
                        tableNumber?.let { stringResource(R.string.common_table_label, it) } ?: stringResource(R.string.home_table_at_checkout)
                    } else {
                        address.ifBlank { stringResource(R.string.home_address_at_checkout) }
                    },
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s)) {
            CircleIconButton(BoucheeIcons.User, contentDescription = stringResource(R.string.home_my_account), onClick = onAccountClick)
            CartIconButton(count = cartCount, onClick = onCartClick)
        }
    }
}

/** Compte connecté : nom, e-mail, gestion des commandes (admin) et déconnexion. */
@Composable
private fun AccountDialog(
    user: UserModel?,
    isAdmin: Boolean,
    onAdminOrders: () -> Unit,
    onSignOut: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(BoucheeIcons.User, contentDescription = null, tint = colors.primary) },
        title = {
            Text(
                when {
                    user?.isAnonymous == true -> stringResource(R.string.home_guest)
                    else -> user?.name?.takeIf { it.isNotBlank() } ?: stringResource(R.string.home_my_account)
                }
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.m)) {
                Text(
                    if (user?.isAnonymous == true) stringResource(R.string.home_guest_subtitle) else user?.email.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
                if (isAdmin) {
                    OutlinedPillButton(text = stringResource(R.string.home_manage_orders), onClick = onAdminOrders, modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSignOut) {
                Icon(BoucheeIcons.Logout, contentDescription = null, tint = colors.primary, modifier = Modifier.size(18.dp))
                Text(stringResource(R.string.common_sign_out), color = colors.primary, modifier = Modifier.padding(start = Spacing.s))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_close), color = colors.onSurface) }
        },
        containerColor = colors.surfaceContainer
    )
}

@Composable
private fun MealOfTheDayCard(meal: MealModel, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = colors.onSurface,
        contentColor = colors.surfaceContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(Spacing.l),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Tag(
                    text = stringResource(R.string.home_meal_of_the_day),
                    background = colors.secondary,
                    contentColor = colors.onSecondary
                )
                Text(meal.name.orEmpty(), style = MaterialTheme.typography.titleLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
                val details = listOfNotNull(
                    areaLabel(meal.area),
                    categoryLabel(meal.category).takeIf { it.isNotBlank() },
                    formatPrice(MealPricing.priceCentsFor(meal.id))
                )
                Text(joinDetails(*details.toTypedArray()), style = MaterialTheme.typography.bodySmall, color = colors.outlineVariant)
            }
            MealImage(
                url = meal.thumbnail,
                contentDescription = meal.name,
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
private fun CategoriesSection(
    categories: List<CategoryModel>,
    highlighted: String?,
    onCategoryClick: (String) -> Unit,
    onSeeAll: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.m)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.home_categories), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            TextButton(onClick = onSeeAll) {
                Text(stringResource(R.string.home_see_all), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(categories, key = { it.id ?: it.hashCode() }) { category ->
                val name = category.name.orEmpty()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.s),
                    modifier = Modifier
                        .width(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Surface(
                        onClick = { onCategoryClick(name) },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = if (name == highlighted) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier.size(64.dp)
                    ) {
                        MealImage(
                            url = category.thumbnail,
                            contentDescription = categoryLabel(name),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        )
                    }
                    // Noms longs (« Fruits de mer », « Accompagnement ») : 2 lignes et police réduite si besoin.
                    BasicText(
                        categoryLabel(name),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = LocalContentColor.current
                        ),
                        maxLines = 2,
                        autoSize = TextAutoSize.StepBased(minFontSize = 9.sp, maxFontSize = MaterialTheme.typography.bodySmall.fontSize),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun HomePreview() {
    FoodAppTheme {
        HomeScreen(
            state = HomeState(
                categories = listOf(
                    CategoryModel("1", "Chicken", null, null),
                    CategoryModel("2", "Beef", null, null),
                    CategoryModel("3", "Pasta", null, null)
                ),
                selectedCategory = "Chicken",
                popularCategory = "Chicken",
                cartCount = 3
            ),
            onIntent = {},
            onTabSelected = {}
        )
    }
}
