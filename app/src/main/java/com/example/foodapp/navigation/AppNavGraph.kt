package com.example.foodapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.foodapp.ui.cart.CartRoute
import com.example.foodapp.ui.checkout.CheckoutRoute
import com.example.foodapp.ui.components.BottomTab
import com.example.foodapp.ui.confirmation.ConfirmationRoute
import com.example.foodapp.ui.details.MealDetailsRoute
import com.example.foodapp.ui.home.HomeRoute
import com.example.foodapp.ui.menu.MenuRoute
import com.example.foodapp.ui.onboarding.OnboardingRoute
import com.example.foodapp.ui.search.SearchRoute

@Composable
fun AppNavGraph(startDestination: String, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val onTabSelected: (BottomTab) -> Unit = { navController.navigateToTab(it) }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingRoute(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeRoute(
                onNavigateToMenu = { navController.navigate(Routes.menu(it)) },
                onNavigateToMealDetails = { navController.navigate(Routes.mealDetails(it)) },
                onNavigateToSearch = { navController.navigate(Routes.SEARCH) },
                onNavigateToCart = { navController.navigate(Routes.CART) },
                onTabSelected = onTabSelected
            )
        }

        composable(
            route = Routes.MENU,
            arguments = listOf(navArgument(Routes.ARG_CATEGORY) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            MenuRoute(
                onNavigateToMealDetails = { navController.navigate(Routes.mealDetails(it)) },
                onNavigateToCart = { navController.navigate(Routes.CART) },
                onNavigateBack = { navController.popBackStack() },
                onTabSelected = onTabSelected
            )
        }

        composable(
            route = Routes.MEAL_DETAILS,
            arguments = listOf(navArgument(Routes.ARG_MEAL_ID) { type = NavType.StringType })
        ) {
            MealDetailsRoute(
                onNavigateToCart = { navController.navigate(Routes.CART) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SEARCH) {
            SearchRoute(
                onNavigateToMealDetails = { navController.navigate(Routes.mealDetails(it)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CART) {
            CartRoute(
                onNavigateToCheckout = { navController.navigate(Routes.CHECKOUT) },
                onNavigateToMenu = { navController.navigateToTab(BottomTab.Menu) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CHECKOUT) {
            CheckoutRoute(
                onNavigateToConfirmation = { orderNumber ->
                    navController.navigate(Routes.confirmation(orderNumber)) {
                        // Après paiement, le retour ne ramène ni au paiement ni au panier.
                        popUpTo(Routes.HOME)
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.CONFIRMATION,
            arguments = listOf(navArgument(Routes.ARG_ORDER_NUMBER) { type = NavType.StringType })
        ) {
            ConfirmationRoute(
                onNavigateToHome = { navController.popBackStack(Routes.HOME, inclusive = false) }
            )
        }
    }
}

/** Navigation de la barre du bas : une seule instance par onglet, l'accueil reste à la base. */
private fun NavHostController.navigateToTab(tab: BottomTab) {
    val route = when (tab) {
        BottomTab.Home -> Routes.HOME
        BottomTab.Menu -> Routes.menu()
        BottomTab.Cart -> Routes.CART
    }
    navigate(route) {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
