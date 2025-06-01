package com.mandarinkafe.mandarin.features.favorites.ui.view_model

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.ui.models.UiError
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState

sealed interface FavoritesContract {
    sealed interface FavoritesEvent : BaseEvent

    sealed interface FavoritesEffect : BaseEffect

    data class FavoritesState(
        val isLoading: Boolean = false,
        val data: List<CustomizedMeal> = emptyList(),
        val error: UiError? = null
    ) : BaseState
}