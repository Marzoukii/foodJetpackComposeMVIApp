package com.example.foodapp.domain.usecase

import com.example.foodapp.data.repository.PreferencesRepository
import javax.inject.Inject

class IsOnboardingCompletedUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    fun execute(): Boolean = preferencesRepository.isOnboardingCompleted()
}

class CompleteOnboardingUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    fun execute() {
        preferencesRepository.completeOnboarding()
    }
}
