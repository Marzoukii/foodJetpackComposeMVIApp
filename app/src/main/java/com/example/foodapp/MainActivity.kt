package com.example.foodapp

import android.os.Bundle
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
        // Splash système (même fond et même logo) le temps du chargement, puis l'écran SPLASH prend le relais.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // À chaque lancement : logo 3 secondes, puis le client choisit sur place ou livraison.
        setContent {
            FoodAppTheme {
                AppNavGraph(startDestination = Routes.SPLASH)
            }
        }
    }
}
