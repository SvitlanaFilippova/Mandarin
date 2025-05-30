package com.mandarinkafe.mandarin.shared.ui.view_model

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEffect
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEffect.OpenMealDetailsBS
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent.HideTopBar
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent.OnMealDetailsClick
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent.OnPhoneClick
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent.ShowTopBar
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedState
import com.mandarinkafe.mandarin.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(private val favoritesApi: FavoritesApi) :
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
            is SharedEvent.ToggleFavorite -> toggleFavorite(event.meal, event.item)
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

    override fun setLoading(isLoading: Boolean) {}
}