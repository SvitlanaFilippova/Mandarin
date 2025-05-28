package com.mandarinkafe.mandarin.features.favorites.ui.view_model

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.BaseViewModel
import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.features.favorites.ui.view_model.FavoritesContract.FavoritesEffect
import com.mandarinkafe.mandarin.features.favorites.ui.view_model.FavoritesContract.FavoritesEvent
import com.mandarinkafe.mandarin.features.favorites.ui.view_model.FavoritesContract.FavoritesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val favoritesApi: FavoritesApi) :
    BaseViewModel<FavoritesEvent, FavoritesEffect, FavoritesState>() {
    override fun setInitialState() = FavoritesState()

    init {
        observeFavorites()
    }

    override fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.ToggleFavorite -> toggleFavorite(event.item)
            is FavoritesEvent.OpenMealDetails -> sendEffect(
                FavoritesEffect.OpenMealDetailsBS(event.item)
            )
        }
    }

    private fun observeFavorites() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            favoritesApi.observeFavorites()
                .collect { favList ->
                    if (favList.isEmpty()) {
                        setState {
                            copy(
                                error =,
                                isLoading = false
                            )
                        }
                    } else {
                        setState { copy(data = favList, isLoading = false, error = null) }
                    }
                }
        }
    }

    // Добавить блюдо в избранное или удалить
    private fun toggleFavorite(meal: CustomizedMeal) {
        viewModelScope.launch {
            favoritesApi.toggleFavorite(meal)
        }
    }
}
