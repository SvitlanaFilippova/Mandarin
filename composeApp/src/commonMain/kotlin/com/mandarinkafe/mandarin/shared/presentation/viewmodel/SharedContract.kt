package com.mandarinkafe.mandarin.shared.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.util.presentation.BaseContract
import dev.icerock.moko.resources.StringResource

sealed interface SharedContract {

    sealed interface SharedEvent : BaseContract.BaseEvent {
        data object OnPhoneClick : SharedEvent
        data object OnLogoClick : SharedEvent
        data object HideTopBar : SharedEvent
        data object ShowTopBar : SharedEvent
        data object ResetTopBar : SharedEvent
        data class ShowFavoriteDialog(val item: CustomizedMeal) : SharedEvent
        data object DismissFavoriteDialog : SharedEvent
        data object RefreshMenuIfStale : SharedEvent
        data class ToggleFavorite(val meal: Meal? = null, val item: CustomizedMeal? = null) :
            SharedEvent

        data class OnMealDetailsClick(
            val meal: Meal? = null,
            val item: CustomizedMeal? = null,
            val cartItem: CartItem? = null,
            val mealId: String? = null,
            val isEditMode: Boolean = false
        ) : SharedEvent

        data class ShowSnackbar(
            val messageRes: StringResource? = null,
            val message: String? = null,
            val showToCartButton: Boolean = false
        ) : SharedEvent

        data object GoBack : SharedEvent
    }

    sealed interface SharedEffect : BaseContract.BaseEffect {
        data object GoBackEffect : SharedEffect
        data object ScrollToTop : SharedEffect
        data object OnPhoneClick : SharedEffect

        data class OpenMealDetailsBS(
            val cartItem: CartItem? = null,
            val meal: Meal? = null,
            val item: CustomizedMeal? = null,
            val mealId: String? = null,
            val isEditMode: Boolean = false
        ) : SharedEffect

        data object FinishSplash : SharedEffect
        data class SnackbarEffect(
            val messageRes: StringResource? = null,
            val message: String? = null,
            val showToCartButton: Boolean = false
        ) : SharedEffect
    }

    data class SharedState(
        val shouldShowTopBar: Boolean = true,
        val showFavoriteDialog: Boolean = false,
        val selectedMealForFavoriteChoice: CustomizedMeal? = null,
        val cartItemsCount: Int = 0,
    ) : BaseContract.BaseState
}

