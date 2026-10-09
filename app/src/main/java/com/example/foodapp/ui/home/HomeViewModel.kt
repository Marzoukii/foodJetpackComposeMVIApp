package com.example.foodapp.ui.home

import androidx.lifecycle.viewModelScope
import com.example.foodapp.R
import com.example.foodapp.data.NetworkResult
import com.example.foodapp.domain.usecase.GetAuthStateUseCase
import com.example.foodapp.domain.usecase.GetCartItemsUseCase
import com.example.foodapp.domain.usecase.GetCategoriesUseCase
import com.example.foodapp.domain.usecase.GetDeliveryAddressUseCase
import com.example.foodapp.domain.usecase.GetMealsByCategoryUseCase
import com.example.foodapp.domain.usecase.GetOrderTypeUseCase
import com.example.foodapp.domain.usecase.GetRandomMealUseCase
import com.example.foodapp.domain.usecase.GetTableNumberUseCase
import com.example.foodapp.domain.usecase.ObserveIsAdminUseCase
import com.example.foodapp.domain.usecase.SignOutUseCase
import com.example.foodapp.domain.usecase.SyncUserProfileUseCase
import com.example.foodapp.ui.base.MviViewModel
import com.example.foodapp.ui.util.toUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getRandomMealUseCase: GetRandomMealUseCase,
    private val getMealsByCategoryUseCase: GetMealsByCategoryUseCase,
    getCartItemsUseCase: GetCartItemsUseCase,
    getDeliveryAddressUseCase: GetDeliveryAddressUseCase,
    getOrderTypeUseCase: GetOrderTypeUseCase,
    getTableNumberUseCase: GetTableNumberUseCase,
    getAuthStateUseCase: GetAuthStateUseCase,
    observeIsAdminUseCase: ObserveIsAdminUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val syncUserProfileUseCase: SyncUserProfileUseCase
) : MviViewModel<HomeState, HomeIntent, HomeEffect>(HomeState()) {

    private var popularJob: Job? = null

    init {
        getCartItemsUseCase.execute()
            .onEach { items -> setState { copy(cartCount = items.sumOf { it.quantity }) } }
            .launchIn(viewModelScope)

        getDeliveryAddressUseCase.execute()
            .onEach { address -> setState { copy(deliveryAddress = address) } }
            .launchIn(viewModelScope)

        getOrderTypeUseCase.execute()
            .onEach { type -> setState { copy(orderType = type) } }
            .launchIn(viewModelScope)

        getTableNumberUseCase.execute()
            .onEach { number -> setState { copy(tableNumber = number) } }
            .launchIn(viewModelScope)

        getAuthStateUseCase.execute()
            .onEach { user -> setState { copy(user = user) } }
            .distinctUntilChangedBy { it?.id }
            .onEach { user -> if (user != null) viewModelScope.launch { syncUserProfileUseCase.execute(user) } }
            .launchIn(viewModelScope)

        observeIsAdminUseCase.execute()
            .onEach { isAdmin -> setState { copy(isAdmin = isAdmin) } }
            .launchIn(viewModelScope)

        onIntent(HomeIntent.Load)
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Load -> load()
            is HomeIntent.CategoryClicked -> selectCategory(intent.categoryName)
            HomeIntent.SeeAllCategoriesClicked -> sendEffect(HomeEffect.NavigateToMenu(null))
            is HomeIntent.MealClicked -> sendEffect(HomeEffect.NavigateToMealDetails(intent.mealId))
            HomeIntent.SearchClicked -> sendEffect(HomeEffect.NavigateToSearch)
            HomeIntent.CartClicked -> sendEffect(HomeEffect.NavigateToCart)
            HomeIntent.AccountClicked -> setState { copy(isAccountDialogVisible = true) }
            HomeIntent.DismissAccountDialog -> setState { copy(isAccountDialogVisible = false) }
            HomeIntent.SignOutClicked -> {
                setState { copy(isAccountDialogVisible = false) }
                signOutUseCase.execute()
                sendEffect(HomeEffect.NavigateToOrderMode(clearBackStack = true))
            }
            HomeIntent.ChangeOrderModeClicked -> sendEffect(HomeEffect.NavigateToOrderMode(clearBackStack = false))
            HomeIntent.AdminOrdersClicked -> {
                setState { copy(isAccountDialogVisible = false) }
                sendEffect(HomeEffect.NavigateToAdminOrders)
            }
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
                        categories.firstOrNull()?.name?.let { selectCategory(it) }
                    }
                    is NetworkResult.Error -> setState {
                        copy(isLoading = false, error = result.exception.toUiText(R.string.common_unknown_error))
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

    /** Entoure la catégorie tout de suite, puis charge ses plats dans « Populaires ». */
    private fun selectCategory(categoryName: String) {
        if (categoryName == currentState.selectedCategory) return
        setState { copy(selectedCategory = categoryName) }
        loadPopular(categoryName)
    }

    private fun loadPopular(categoryName: String) {
        // Un tap rapide sur une autre catégorie annule le chargement précédent.
        popularJob?.cancel()
        popularJob = getMealsByCategoryUseCase.execute(categoryName)
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
