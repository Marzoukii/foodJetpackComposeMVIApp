package com.example.foodapp.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.foodapp.R
import com.example.foodapp.data.NetworkResult
import com.example.foodapp.domain.pricing.MealPricing
import com.example.foodapp.domain.usecase.AddToCartUseCase
import com.example.foodapp.domain.usecase.GetMealDetailsUseCase
import com.example.foodapp.navigation.Routes
import com.example.foodapp.ui.base.MviViewModel
import com.example.foodapp.ui.util.toUiText
import com.example.foodapp.ui.util.uiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMealDetailsUseCase: GetMealDetailsUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : MviViewModel<MealDetailsState, MealDetailsIntent, MealDetailsEffect>(
    savedStateHandle.get<String>(Routes.ARG_MEAL_ID).orEmpty().let { id ->
        MealDetailsState(mealId = id, unitPriceCents = MealPricing.priceCentsFor(id))
    }
) {

    init {
        onIntent(MealDetailsIntent.LoadMeal)
    }

    override fun onIntent(intent: MealDetailsIntent) {
        when (intent) {
            MealDetailsIntent.LoadMeal -> loadMeal()
            MealDetailsIntent.IncrementQuantity -> setState {
                copy(quantity = (quantity + 1).coerceAtMost(MAX_QUANTITY))
            }
            MealDetailsIntent.DecrementQuantity -> setState {
                copy(quantity = (quantity - 1).coerceAtLeast(1))
            }
            MealDetailsIntent.ToggleRecipe -> setState { copy(showFullRecipe = !showFullRecipe) }
            is MealDetailsIntent.ToggleIngredient -> setState {
                copy(
                    removedIngredients = if (intent.ingredient in removedIngredients) removedIngredients - intent.ingredient
                    else removedIngredients + intent.ingredient
                )
            }
            MealDetailsIntent.AddToCartClicked -> addToCart()
            MealDetailsIntent.BackClicked -> sendEffect(MealDetailsEffect.NavigateBack)
        }
    }

    private fun loadMeal() {
        getMealDetailsUseCase.execute(currentState.mealId)
            .onStart { setState { copy(isLoading = true, error = null) } }
            .onEach { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val meal = result.data?.meals?.firstOrNull()
                        setState {
                            copy(
                                isLoading = false,
                                meal = meal,
                                error = if (meal == null) uiText(R.string.details_not_found) else null
                            )
                        }
                    }
                    is NetworkResult.Error -> setState {
                        copy(isLoading = false, error = result.exception.toUiText(R.string.common_unknown_error))
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun addToCart() {
        val meal = currentState.meal ?: return
        viewModelScope.launch {
            addToCartUseCase.execute(
                mealId = currentState.mealId,
                name = meal.name.orEmpty(),
                thumbnail = meal.thumbnail,
                quantity = currentState.quantity,
                removedIngredients = currentState.removedIngredients
            )
            sendEffect(MealDetailsEffect.NavigateToCart)
        }
    }

    private companion object {
        const val MAX_QUANTITY = 20
    }
}
