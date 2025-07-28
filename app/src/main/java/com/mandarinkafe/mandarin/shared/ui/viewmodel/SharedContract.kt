package com.mandarinkafe.mandarin.shared.ui.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState

sealed interface SharedContract {

    sealed interface SharedEvent : BaseEvent {
        data object OnPhoneClick : SharedEvent
        data object HideTopBar : SharedEvent
        data object ShowTopBar : SharedEvent
        data object ResetTopBar : SharedEvent
        data class ShowFavoriteDialog(val item: CustomizedMeal) : SharedEvent
        data object DismissFavoriteDialog : SharedEvent
        data class ToggleFavorite(val meal: Meal? = null, val item: CustomizedMeal? = null) :
            SharedEvent

        data class OnMealDetailsClick(
            val meal: Meal? = null,
            val item: CustomizedMeal? = null,
            val isEditMode: Boolean = false
        ) : SharedEvent

        data class OnEditMealClick(
            val item: CustomizedMeal
        ) : SharedEvent

        data object GoBack : SharedEvent

    }

    sealed interface SharedEffect : BaseEffect {
        data object GoBackEffect : SharedEffect
        data object OnPhoneClick : SharedEffect
        data class OpenMealDetailsBS(
            val meal: Meal? = null,
            val item: CustomizedMeal? = null,
            val isEditMode: Boolean = false
        ) : SharedEffect
        data object FinishSplash : SharedEffect
    }

    data class SharedState(
        val shouldShowTopBar: Boolean = true,
        val showFavoriteDialog: Boolean = false,
        val selectedMealForFavoriteChoice: CustomizedMeal? = null,
        val cartItemsCount: Int = 0,
    ) : BaseState
}