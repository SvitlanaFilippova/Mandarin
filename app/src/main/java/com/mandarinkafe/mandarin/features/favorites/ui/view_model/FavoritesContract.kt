package com.mandarinkafe.mandarin.features.favorites.ui.view_model

import com.mandarinkafe.mandarin.core.BaseEffect
import com.mandarinkafe.mandarin.core.BaseEvent
import com.mandarinkafe.mandarin.core.BaseState
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.util.ui.BottomSheetEffect

sealed interface FavoritesContract {
    sealed interface FavoritesEvent : BaseEvent {
        data class ToggleFavorite(val meal: CustomizedMeal) : FavoritesEvent
    }

    sealed interface FavoritesEffect : BaseEffect {
        data class OpenMealDetailsBS(
            val item: CustomizedMeal
        ) :
            FavoritesEffect, BottomSheetEffect
    }

    data class FavoritesState(
        val isLoading: Boolean = false,
        val data: List<CustomizedMeal> = emptyList(),
    ) : BaseState
}