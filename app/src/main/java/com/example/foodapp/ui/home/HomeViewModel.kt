package com.example.foodapp.ui.home

import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.NetworkResult
import com.example.foodapp.domain.usecase.GetCartItemsUseCase
import com.example.foodapp.domain.usecase.GetCategoriesUseCase
import com.example.foodapp.domain.usecase.GetDeliveryAddressUseCase
import com.example.foodapp.domain.usecase.GetMealsByCategoryUseCase
import com.example.foodapp.domain.usecase.GetRandomMealUseCase
import com.example.foodapp.ui.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getRandomMealUseCase: GetRandomMealUseCase,
    private val getMealsByCategoryUseCase: GetMealsByCategoryUseCase,
    getCartItemsUseCase: GetCartItemsUseCase,
    getDeliveryAddressUseCase: GetDeliveryAddressUseCase
) : MviViewModel<HomeState, HomeIntent, HomeEffect>(HomeState()) {

    init {
        getCartItemsUseCase.execute()
            .onEach { items -> setState { copy(cartCount = items.sumOf { it.quantity }) } }
            .launchIn(viewModelScope)

        getDeliveryAddressUseCase.execute()
            .onEach { address -> setState { copy(deliveryAddress = address) } }
            .launchIn(viewModelScope)

        onIntent(HomeIntent.Load)
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Load -> load()
            is HomeIntent.CategoryClicked -> sendEffect(HomeEffect.NavigateToMenu(intent.categoryName))
            HomeIntent.SeeAllCategoriesClicked -> sendEffect(HomeEffect.NavigateToMenu(null))
            is HomeIntent.MealClicked -> sendEffect(HomeEffect.NavigateToMealDetails(intent.mealId))
            HomeIntent.SearchClicked -> sendEffect(HomeEffect.NavigateToSearch)
            HomeIntent.CartClicked -> sendEffect(HomeEffect.NavigateToCart)
        }
    }

    private fun load() {
        getCategoriesUseCase.execute()
            .onStart { setState { copy(isLoading = true, error = null) } }
            .onEach { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val categories = result.data?.categories.orEmpty()
                        setState { copy(isLoading = false, categories = categories) }
                        categories.firstOrNull()?.name?.let { loadPopular(it) }
                    }
                    is NetworkResult.Error -> setState {
                        copy(isLoading = false, error = result.exception.message ?: "Erreur inconnue")
                    }
                }
            }
            .launchIn(viewModelScope)

        getRandomMealUseCase.execute()
            .onEach { result ->
                if (result is NetworkResult.Success) {
                    setState { copy(mealOfTheDay = result.data?.meals?.firstOrNull()) }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadPopular(categoryName: String) {
        getMealsByCategoryUseCase.execute(categoryName)
            .onEach { result ->
                if (result is NetworkResult.Success) {
                    setState {
                        copy(
                            popularCategory = categoryName,
                            popularMeals = result.data?.meals.orEmpty().take(POPULAR_COUNT)
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private companion object {
        const val POPULAR_COUNT = 6
    }
}
