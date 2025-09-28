package com.mandarinkafe.mandarin.features.favorites.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.favorites.presentation.viewmodel.FavoritesContract.FavoritesEffect
import com.mandarinkafe.mandarin.features.favorites.presentation.viewmodel.FavoritesContract.FavoritesEvent
import com.mandarinkafe.mandarin.features.favorites.presentation.viewmodel.FavoritesContract.FavoritesState
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorEmptyData
import com.mandarinkafe.mandarin.util.Resource.ErrorNoInternet
import com.mandarinkafe.mandarin.util.Resource.ErrorOther
import com.mandarinkafe.mandarin.util.Resource.Idle
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.Resource.Success
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
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
            FavoritesEvent.ForceRefresh -> forceRefresh()
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }

    private fun forceRefresh() {
        viewModelScope.launch {
            favoritesApi.forceRefresh()
        }
    }

    private fun observeFavorites() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            favoritesApi.observeFavoritesItems()
                .collect { resource ->
                    setLoading(resource is Loading)
                    when (resource) {
                        is Success -> {
                            setData(resource.data)
                        }

                        is Loading -> {}
                        is Idle -> {}
                        else -> {
                            setError(resource)
                            if (resource.message?.isNotEmpty() == true) {
                                sendEffect(FavoritesEffect.ShowSnackbar(message = resource.message))
                            }
                        }
                    }
                }
        }
    }

    private fun setData(data: List<CustomizedMeal>?) {
        if (data != null) {
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
