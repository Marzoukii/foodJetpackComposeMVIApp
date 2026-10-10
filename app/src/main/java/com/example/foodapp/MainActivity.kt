package com.example.foodapp

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.foodapp.navigation.AppNavGraph
import com.example.foodapp.navigation.Routes
import com.example.foodapp.ui.theme.FoodAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        // Le splash reste visible un court instant, même si l'appli démarre vite.
        val splashEnd = SystemClock.uptimeMillis() + SPLASH_DURATION_MS
        splashScreen.setKeepOnScreenCondition { savedInstanceState == null && SystemClock.uptimeMillis() < splashEnd }
        enableEdgeToEdge()
        // À chaque lancement, le client choisit : sur place ou livraison.
        setContent {
            FoodAppTheme {
                AppNavGraph(startDestination = Routes.ORDER_MODE)
            }
        }
    }

    private companion object {
        const val SPLASH_DURATION_MS = 1000L
    }
}
