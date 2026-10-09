package com.example.foodapp.ui.menu

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.foodapp.R
import com.example.foodapp.data.NetworkResult
import com.example.foodapp.domain.usecase.AddToCartUseCase
import com.example.foodapp.domain.usecase.GetCartItemsUseCase
import com.example.foodapp.domain.usecase.GetCategoriesUseCase
import com.example.foodapp.domain.usecase.GetMealsByCategoryUseCase
import com.example.foodapp.navigation.Routes
import com.example.foodapp.ui.base.MviViewModel
import com.example.foodapp.ui.util.toUiText
import com.example.foodapp.ui.util.uiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getMealsByCategoryUseCase: GetMealsByCategoryUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    getCartItemsUseCase: GetCartItemsUseCase
) : MviViewModel<MenuState, MenuIntent, MenuEffect>(
    MenuState(selectedCategory = savedStateHandle.get<String>(Routes.ARG_CATEGORY))
) {

    private var mealsJob: Job? = null

    init {
        getCartItemsUseCase.execute()
            .onEach { items ->
                setState {
                    copy(
                        cartCount = items.sumOf { it.quantity },
                        cartTotalCents = items.sumOf { it.lineTotalCents }
                    )
                }
            }
            .launchIn(viewModelScope)

        loadCategories()
    }

    override fun onIntent(intent: MenuIntent) {
        when (intent) {
            MenuIntent.Retry -> if (currentState.categories.isEmpty()) loadCategories() else loadMeals()
            is MenuIntent.CategorySelected -> if (intent.categoryName != currentState.selectedCategory) {
                setState { copy(selectedCategory = intent.categoryName) }
                loadMeals()
            }
            is MenuIntent.MealClicked -> sendEffect(MenuEffect.NavigateToMealDetails(intent.mealId))
            is MenuIntent.AddToCartClicked -> addToCart(intent)
            MenuIntent.CartClicked -> sendEffect(MenuEffect.NavigateToCart)
            MenuIntent.BackClicked -> sendEffect(MenuEffect.NavigateBack)
        }
    }

    private fun loadCategories() {
        getCategoriesUseCase.execute()
            .onStart { setState { copy(isLoading = true, error = null) } }
            .onEach { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val names = result.data?.categories.orEmpty().mapNotNull { it.name }
                        setState {
                            copy(categories = names, selectedCategory = selectedCategory ?: names.firstOrNull())
                        }
                        loadMeals()
                    }
                    is NetworkResult.Error -> setState {
                        copy(isLoading = false, error = result.exception.toUiText(R.string.common_unknown_error))
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadMeals() {
        val category = currentState.selectedCategory ?: return
        mealsJob?.cancel()
        mealsJob = getMealsByCategoryUseCase.execute(category)
            .onStart { setState { copy(isLoading = true, error = null, meals = emptyList()) } }
            .onEach { result ->
                when (result) {
                    is NetworkResult.Success -> setState {
                        copy(isLoading = false, meals = result.data?.meals.orEmpty())
                    }
                    is NetworkResult.Error -> setState {
                        copy(isLoading = false, error = result.exception.toUiText(R.string.common_unknown_error))
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun addToCart(intent: MenuIntent.AddToCartClicked) {
        val meal = intent.meal
        val id = meal.id ?: return
        viewModelScope.launch {
            addToCartUseCase.execute(id, meal.name.orEmpty(), meal.thumbnail)
            sendEffect(MenuEffect.ShowMessage(uiText(R.string.common_added_to_cart, meal.name.orEmpty())))
        }
    }
}
