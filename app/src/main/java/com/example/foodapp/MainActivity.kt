package com.example.foodapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.foodapp.domain.usecase.GetCurrentUserUseCase
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

    @Inject
    lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val startDestination = when {
            !isOnboardingCompletedUseCase.execute() -> Routes.ONBOARDING
            getCurrentUserUseCase.execute() == null -> Routes.LOGIN
            else -> Routes.HOME
        }
        setContent {
            FoodAppTheme {
                AppNavGraph(startDestination = startDestination)
            }
        }
    }
}
