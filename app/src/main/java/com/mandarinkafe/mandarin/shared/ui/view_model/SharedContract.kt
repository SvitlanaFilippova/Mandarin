package com.mandarinkafe.mandarin.shared.ui.view_model

import com.mandarinkafe.mandarin.core.BaseEffect
import com.mandarinkafe.mandarin.core.BaseEvent
import com.mandarinkafe.mandarin.core.BaseState
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.util.ui.BottomSheetEffect

sealed interface SharedContract {

    sealed interface SharedEvent : BaseEvent {
        data object HideTopBar : SharedEvent
        data object ShowTopBar : SharedEvent
        data object ResetTopBar : SharedEvent
        data class OnMealDetailsClick(val meal: Meal? = null, val item: CustomizedMeal? = null) :
            SharedEvent
        data object OnPhoneClick : SharedEvent
        data class MealFavoriteChanged(val mealId: String, val isFavorite: Boolean) : SharedEvent
    }

    sealed interface SharedEffect : BaseEffect {
        data class OpenMealDetailsBS(val meal: Meal? = null, val item: CustomizedMeal? = null) :
            BottomSheetEffect, SharedEffect

        data object OnPhoneClick : SharedEffect
    }

    data class SharedState(
        val shouldShowTopBar: Boolean = true,
        val lastChangedFavorite: Pair<String, Boolean>? = null,
    ) : BaseState
}