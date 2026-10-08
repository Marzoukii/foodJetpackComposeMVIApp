package com.example.foodapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.foodapp.domain.usecase.IsOnboardingCompletedUseCase
import com.example.foodapp.navigation.AppNavGraph
import com.example.foodapp.navigation.Routes
import com.example.foodapp.ui.theme.FoodAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var isOnboardingCompletedUseCase: IsOnboardingCompletedUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val startDestination = if (isOnboardingCompletedUseCase.execute()) Routes.HOME else Routes.ONBOARDING
        setContent {
            FoodAppTheme {
                AppNavGraph(startDestination = startDestination)
            }
        }
    }
}
