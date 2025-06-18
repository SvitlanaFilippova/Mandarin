package com.mandarinkafe.mandarin.shared.ui.view_model

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.core.domain.api.GetInitialDataUseCase
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEffect
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEffect.OpenMealDetailsBS
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent.DismissFavoriteDialog
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent.HideTopBar
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent.OnMealDetailsClick
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent.OnPhoneClick
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent.ResetTopBar
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent.ShowFavoriteDialog
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent.ShowTopBar
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedState
import com.mandarinkafe.mandarin.util.Constants.SPLASH_SCREEN_DURATION
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val favoritesApi: FavoritesApi,
    private val getInitialDataUseCase: GetInitialDataUseCase
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
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val favoritesIDs: StateFlow<Set<String>> =
        favoritesApi.observeFavoritesBaseMealIDs().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet()
        )

    init {
        loadInitialData()
    }

    override fun onEvent(event: SharedEvent) {
        when (event) {
            is HideTopBar -> setState { copy(shouldShowTopBar = false) }
            is ShowTopBar -> setState { copy(shouldShowTopBar = true) }
            is ResetTopBar -> setState { copy(shouldShowTopBar = true) }
            is OnPhoneClick -> sendEffect(SharedEffect.OnPhoneClick)
            is OnMealDetailsClick -> {
                sendEffect(
                    OpenMealDetailsBS(
                        event.meal,
                        event.item,
                    )
                )
            }

            is SharedEvent.OnEditMealClick -> {
                sendEffect(OpenMealDetailsBS(item = event.item, isEditMode = true))
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

        }
    }

    private fun toggleFavorite(meal: Meal?, item: CustomizedMeal?) {
        viewModelScope.launch {
            if (meal != null) {
                favoritesApi.toggleFavorite(meal)
            } else if (item != null) {
                favoritesApi.toggleFavorite(item)
            } else return@launch
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Запускаем параллельно таймер, который через SPLASH_SCREEN_DURATION закроет экран,
            launch {
                delay(SPLASH_SCREEN_DURATION)
                setState {
                    copy(isSplashVisible = false)
                }
            }

            // Параллельно начинаем загрузку меню
            getInitialDataUseCase().collectLatest { resource ->
                if (resource is Resource.Success) {
                    setState { copy(isSplashVisible = false) }
                    // Если Success прилетит раньше таймера SPLASH_SCREEN_DURATION - закрываем экран
                }
            }
        }
    }

    override fun setLoading(isLoading: Boolean) {}
}