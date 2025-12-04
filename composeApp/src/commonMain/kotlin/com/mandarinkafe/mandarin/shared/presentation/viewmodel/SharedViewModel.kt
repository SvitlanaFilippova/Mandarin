package com.mandarinkafe.mandarin.shared.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.data.api.RefreshMenuIfStaleUseCase
import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.core.domain.api.GetInitialDataUseCase
import com.mandarinkafe.mandarin.core.domain.api.ObserveCartCountUseCase
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEffect
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEffect.FinishSplash
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEffect.GoBackEffect
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEffect.OpenMealDetailsBS
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEffect.ScrollToTop
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEffect.SnackbarEffect
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEvent.DismissFavoriteDialog
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEvent.HideTopBar
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEvent.OnMealDetailsClick
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEvent.OnPhoneClick
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEvent.ResetTopBar
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEvent.ShowFavoriteDialog
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEvent.ShowTopBar
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedState
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.Constants.SPLASH_SCREEN_DURATION
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SharedViewModel(
    private val favoritesApi: FavoritesApi,
    private val getInitialDataUseCase: GetInitialDataUseCase,
    private val observeCartCountUseCase: ObserveCartCountUseCase,
    private val refreshMenuIfStaleUseCase: RefreshMenuIfStaleUseCase,
    authRepository: AuthRepository,
) :
    BaseViewModel<SharedEvent, SharedEffect, SharedState>() {
    override fun setInitialState() = SharedState()


    val favoritesItemsFlow: StateFlow<List<CustomizedMeal>> =
        favoritesApi.observeFavoritesItems()
            .map { resource ->
                when (resource) {
                    is Resource.Success -> resource.data ?: emptyList()
                    else -> emptyList()
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(SHARING_STOP_TIMEOUT_MILLIS),
                initialValue = emptyList()
            )

    val favoritesIDs: StateFlow<Set<String>> =
        favoritesApi.observeFavoritesBaseMealIDs().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_STOP_TIMEOUT_MILLIS),
            initialValue = emptySet()
        )

    val isAuthorized: StateFlow<Boolean> =
        authRepository.authState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_STOP_TIMEOUT_MILLIS),
            initialValue = false
        )

    init {
        loadInitialData()
        observeCartCount()
    }

    override fun onEvent(event: SharedEvent) {
        when (event) {
            is HideTopBar -> setState { copy(shouldShowTopBar = false) }
            is ShowTopBar -> setState { copy(shouldShowTopBar = true) }
            is ResetTopBar -> setState { copy(shouldShowTopBar = true) }
            is OnPhoneClick -> onPhoneClick(event.phone)
            is SharedEvent.OnLogoClick -> sendEffect(ScrollToTop)
            is OnMealDetailsClick -> {
                sendEffect(
                    OpenMealDetailsBS(
                        meal = event.meal,
                        item = event.item,
                        cartItem = event.cartItem,
                        mealId = event.mealId,
                        isEditMode = event.isEditMode
                    )
                )
            }

            is SharedEvent.ToggleFavorite -> toggleFavorite(event.meal, event.item)

            is DismissFavoriteDialog -> setState {
                copy(
                    showFavoriteDialog = false,
                    selectedMealForFavoriteChoice = null
                )
            }

            is ShowFavoriteDialog -> setState {
                copy(
                    showFavoriteDialog = true,
                    selectedMealForFavoriteChoice = event.item
                )
            }

            is SharedEvent.GoBack -> sendEffect(GoBackEffect)
            is SharedEvent.ShowSnackbar -> sendEffect(
                SnackbarEffect(
                    messageRes = event.messageRes,
                    message = event.message,
                    showToCartButton = event.showToCartButton
                )
            )

            is SharedEvent.RefreshMenuIfStale -> viewModelScope.launch { refreshMenuIfStaleUseCase() }
        }
    }

    private fun onPhoneClick(phone: String?) {
        sendEffect(SharedEffect.OnPhoneClick(phone ?: Constants.MANDARIN_PHONE))

    }

    private fun toggleFavorite(meal: Meal?, item: CustomizedMeal?) {
        viewModelScope.launch {
            if (meal != null) {
                favoritesApi.toggleFavorite(meal)
            } else if (item != null) {
                favoritesApi.toggleFavorite(item)
            } else {
                return@launch
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val done = CompletableDeferred<Unit>()

            // Таймер в отдельной корутине
            launch {
                delay(SPLASH_SCREEN_DURATION)
                done.complete(Unit)
            }

            // Сбор из Flow в отдельной корутине, ждём первого успеха
            launch {
                try {
                    getInitialDataUseCase()
                        .filterIsInstance<Resource.Success<*>>()
                        .first()
                    done.complete(Unit)
                } catch (e: Exception) {
                    done.complete(Unit)
                    Napier.e("SharedViewModel, loadInitialData error: $e")
                }
            }

            done.await()
            setState { copy(isSplashCompleted = true) }
            sendEffect(FinishSplash)
        }
    }

    private fun observeCartCount() {
        viewModelScope.launch {
            observeCartCountUseCase().collect { count ->
                setState { copy(cartItemsCount = count) }
            }
        }
    }

    override fun setLoading(isLoading: Boolean) {
        // не используется в SharedViewModel
    }

    companion object {
        private const val SHARING_STOP_TIMEOUT_MILLIS = 5000L
    }

}

