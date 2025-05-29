package com.mandarinkafe.mandarin.shared.ui.view_model

import com.mandarinkafe.mandarin.core.BaseViewModel
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEffect
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEffect.OpenMealDetailsBS
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent.HideTopBar
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent.OnMealDetailsClick
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent.OnPhoneClick
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent.ShowTopBar
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() :
    BaseViewModel<SharedEvent, SharedEffect, SharedState>() {
    override fun setInitialState() = SharedState()

    override fun onEvent(event: SharedEvent) {
        when (event) {
            is HideTopBar -> setState { copy(shouldShowTopBar = false) }
            is ShowTopBar -> setState { copy(shouldShowTopBar = true) }
            is SharedEvent.ResetTopBar -> setState { copy(shouldShowTopBar = true) }
            is OnPhoneClick -> sendEffect(SharedEffect.OnPhoneClick)
            is OnMealDetailsClick -> sendEffect(
                OpenMealDetailsBS(
                    event.meal,
                    event.item
                )
            )

            is SharedEvent.MealFavoriteChanged -> setState {
                copy(lastChangedFavorite = event.mealId to event.isFavorite)
            }
        }
    }

    override fun setLoading(isLoading: Boolean) {}
}