package com.mandarinkafe.mandarin.features.favorites.ui.view_model

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.BaseViewModel
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.features.favorites.domain.usecase.FavoritesInteractor
import com.mandarinkafe.mandarin.features.favorites.ui.view_model.FavoritesContract.FavoritesEffect
import com.mandarinkafe.mandarin.features.favorites.ui.view_model.FavoritesContract.FavoritesEvent
import com.mandarinkafe.mandarin.features.favorites.ui.view_model.FavoritesContract.FavoritesState
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoritesInteractor: FavoritesInteractor) :
    BaseViewModel<FavoritesEvent, FavoritesEffect, FavoritesState>() {
    override fun setInitialState() = FavoritesState()

    init {
        getFavorites()
    }

    override fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.ToggleFavorite -> toggleFavorite(event.meal)
        }
    }

    private fun getFavorites(): List<CustomizedMeal> {
        val favorites = mutableListOf<CustomizedMeal>()
        viewModelScope.launch {
            favoritesInteractor.getFavorites()
        }
        return favorites
    }

    // Добавить блюдо в избранное или удалить
    private fun toggleFavorite(meal: CustomizedMeal) {
        viewModelScope.launch {
            favoritesInteractor.toggleFavorite(meal)
        }
//            setState {
////                val updatedData = data.map { currentMeal ->
////                    if (currentMeal == meal) {
////                        currentMeal.copy(isFavorite = isNowFavorite)
////                    } else {
////                        currentMeal
////                    }
////                }
////
////                copy(
////                    data = updatedData,
////                )
////            }
//        }
    }
}