package com.example.foodapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.foodapp.ui.admin.AdminOrdersRoute
import com.example.foodapp.ui.admin.AdminTeamRoute
import com.example.foodapp.ui.cart.CartRoute
import com.example.foodapp.ui.checkout.CheckoutRoute
import com.example.foodapp.ui.components.BottomTab
import com.example.foodapp.ui.confirmation.ConfirmationRoute
import com.example.foodapp.ui.details.MealDetailsRoute
import com.example.foodapp.ui.home.HomeRoute
import com.example.foodapp.ui.login.LoginRoute
import com.example.foodapp.ui.menu.MenuRoute
import com.example.foodapp.ui.onboarding.OnboardingRoute
import com.example.foodapp.ui.ordermode.OrderModeRoute
import com.example.foodapp.ui.register.RegisterRoute
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
                onNavigateToOrderMode = {
                    navController.navigate(Routes.ORDER_MODE) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        // Démarrage : sur place (sans compte) ou livraison (connexion imposée).
        composable(Routes.ORDER_MODE) {
            OrderModeRoute(
                onNavigateToHome = { navController.navigateClearingBackStack(Routes.HOME) },
                // L'écran de choix reste dessous : retour = changer de mode.
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.LOGIN) {
            LoginRoute(
                onNavigateToHome = { navController.navigateClearingBackStack(Routes.HOME) },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterRoute(
                onNavigateToHome = { navController.navigateClearingBackStack(Routes.HOME) },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeRoute(
                onNavigateToOrderMode = { clearBackStack ->
                    if (clearBackStack) {
                        navController.navigateClearingBackStack(Routes.ORDER_MODE)
                    } else {
                        navController.navigate(Routes.ORDER_MODE)
                    }
                },
                onNavigateToMenu = { navController.navigate(Routes.menu(it)) },
                onNavigateToMealDetails = { navController.navigate(Routes.mealDetails(it)) },
                onNavigateToSearch = { navController.navigate(Routes.SEARCH) },
                onNavigateToCart = { navController.navigate(Routes.CART) },
                onNavigateToAdminOrders = { navController.navigate(Routes.ADMIN_ORDERS) },
                onTabSelected = onTabSelected
            )
        }

        composable(Routes.ADMIN_ORDERS) {
            AdminOrdersRoute(
                onNavigateToTeam = { navController.navigate(Routes.ADMIN_TEAM) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ADMIN_TEAM) {
            AdminTeamRoute(onNavigateBack = { navController.popBackStack() })
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
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
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

/** Connexion / déconnexion : la destination devient la seule de la pile (retour = quitter l'app). */
private fun NavHostController.navigateClearingBackStack(route: String) {
    navigate(route) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

/** Navigation de la barre du bas : une seule instance par onglet, l'accueil reste à la base. */
private fun NavHostController.navigateToTab(tab: BottomTab) {
    if (tab == BottomTab.Home) {
        // L'accueil est la base de la pile : on y revient simplement. Avec saveState/restoreState,
        // l'onglet quitté serait sauvegardé sous l'accueil puis aussitôt restauré (le clic semblerait sans effet).
        if (currentDestination?.route == Routes.HOME) return
        if (!popBackStack(Routes.HOME, inclusive = false)) navigateClearingBackStack(Routes.HOME)
        return
    }
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
