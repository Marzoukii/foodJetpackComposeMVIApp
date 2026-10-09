package com.example.foodapp.ui.search

import androidx.lifecycle.viewModelScope
import com.example.foodapp.R
import com.example.foodapp.data.NetworkResult
import com.example.foodapp.domain.usecase.AddToCartUseCase
import com.example.foodapp.domain.usecase.SearchMealsUseCase
import com.example.foodapp.ui.base.MviViewModel
import com.example.foodapp.ui.util.toUiText
import com.example.foodapp.ui.util.uiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMealsUseCase: SearchMealsUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : MviViewModel<SearchState, SearchIntent, SearchEffect>(SearchState()) {

    private var searchJob: Job? = null

    override fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.QueryChanged -> {
                setState { copy(query = intent.query) }
                search(intent.query, debounce = true)
            }
            SearchIntent.Retry -> search(currentState.query, debounce = false)
            is SearchIntent.MealClicked -> sendEffect(SearchEffect.NavigateToMealDetails(intent.mealId))
            is SearchIntent.AddToCartClicked -> addToCart(intent)
            SearchIntent.BackClicked -> sendEffect(SearchEffect.NavigateBack)
        }
    }

    private fun search(query: String, debounce: Boolean) {
        searchJob?.cancel()

        if (query.isBlank()) {
            setState { copy(isLoading = false, results = emptyList(), hasSearched = false, error = null) }
            return
        }

        searchJob = viewModelScope.launch {
            if (debounce) delay(DEBOUNCE_MS)
            setState { copy(isLoading = true, error = null) }
            searchMealsUseCase.execute(query.trim()).collect { result ->
                when (result) {
                    is NetworkResult.Success -> setState {
                        copy(isLoading = false, hasSearched = true, results = result.data?.meals.orEmpty())
                    }
                    is NetworkResult.Error -> setState {
                        copy(isLoading = false, error = result.exception.toUiText(R.string.common_unknown_error))
                    }
                }
            }
        }
    }

    private fun addToCart(intent: SearchIntent.AddToCartClicked) {
        val meal = intent.meal
        val id = meal.id ?: return
        viewModelScope.launch {
            addToCartUseCase.execute(id, meal.name.orEmpty(), meal.thumbnail)
            sendEffect(SearchEffect.ShowMessage(uiText(R.string.common_added_to_cart, meal.name.orEmpty())))
        }
    }

    private companion object {
        const val DEBOUNCE_MS = 300L
    }
}
