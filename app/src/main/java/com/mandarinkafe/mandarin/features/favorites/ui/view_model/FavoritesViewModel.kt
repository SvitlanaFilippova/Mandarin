package com.mandarinkafe.mandarin.features.favorites.ui.view_model

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.ui.models.UiError
import com.mandarinkafe.mandarin.features.favorites.ui.view_model.FavoritesContract.FavoritesEffect
import com.mandarinkafe.mandarin.features.favorites.ui.view_model.FavoritesContract.FavoritesEvent
import com.mandarinkafe.mandarin.features.favorites.ui.view_model.FavoritesContract.FavoritesState
import com.mandarinkafe.mandarin.util.BaseViewModel
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorEmptyData
import com.mandarinkafe.mandarin.util.Resource.ErrorNoInternet
import com.mandarinkafe.mandarin.util.Resource.ErrorOther
import com.mandarinkafe.mandarin.util.Resource.Idle
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.Resource.Success
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
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }

    private fun observeFavorites() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            favoritesApi.observeFavoritesItems()
                .collect { resource ->
                    setLoading(resource is Loading)
                    when (resource) {
                        is Success -> setData(resource.data)
                        is Loading -> {}
                        is Idle -> {}
                        else -> setError(resource)
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

    private fun setData(data: List<CustomizedMeal>?) {
        if (!data.isNullOrEmpty()) {
            setState {
                copy(
                    data = data,
                    error = null
                )
            }
        }
    }

    private fun setError(resource: Resource<*>) {
        val error = when (resource) {
            is ErrorEmptyData<*> -> UiError.FavoritesEmpty
            is ErrorNoInternet<*> -> UiError.NoInternet
            is ErrorOther<*> -> UiError.OtherError
            else -> return
        }
        setState { copy(error = error) }
    }
}
