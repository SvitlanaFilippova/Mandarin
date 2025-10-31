package com.mandarinkafe.mandarin.features.favorites.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.util.presentation.BaseContract

sealed interface FavoritesContract {
    sealed interface FavoritesEvent : BaseContract.BaseEvent {
        data object ForceRefresh : FavoritesEvent
    }

    sealed interface FavoritesEffect : BaseContract.BaseEffect {
        data class ShowSnackbar(val message: String) : FavoritesEffect
    }

    data class FavoritesState(
        val isLoading: Boolean = false,
        val data: List<CustomizedMeal> = emptyList(),
        val error: UiError? = null,
    ) : BaseContract.BaseState
}





