package com.example.foodapp.ui.onboarding

data class OnboardingState(
    /** Image d'un plat au hasard (random.php) au centre de l'illustration. */
    val heroImageUrl: String? = null
)

sealed interface OnboardingIntent {
    data object StartClicked : OnboardingIntent
    data object AlreadyHaveAccountClicked : OnboardingIntent
}

sealed interface OnboardingEffect {
    data object NavigateToOrderMode : OnboardingEffect
    data object NavigateToLogin : OnboardingEffect
}
