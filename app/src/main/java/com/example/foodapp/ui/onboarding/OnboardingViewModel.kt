package com.example.foodapp.ui.onboarding

import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.NetworkResult
import com.example.foodapp.domain.usecase.CompleteOnboardingUseCase
import com.example.foodapp.domain.usecase.GetRandomMealUseCase
import com.example.foodapp.ui.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    getRandomMealUseCase: GetRandomMealUseCase,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase
) : MviViewModel<OnboardingState, OnboardingIntent, OnboardingEffect>(OnboardingState()) {

    init {
        getRandomMealUseCase.execute()
            .onEach { result ->
                if (result is NetworkResult.Success) {
                    setState { copy(heroImageUrl = result.data?.meals?.firstOrNull()?.thumbnail) }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: OnboardingIntent) {
        when (intent) {
            OnboardingIntent.StartClicked -> {
                completeOnboardingUseCase.execute()
                sendEffect(OnboardingEffect.NavigateToOrderMode)
            }
            OnboardingIntent.AlreadyHaveAccountClicked -> {
                completeOnboardingUseCase.execute()
                sendEffect(OnboardingEffect.NavigateToLogin)
            }
        }
    }
}
