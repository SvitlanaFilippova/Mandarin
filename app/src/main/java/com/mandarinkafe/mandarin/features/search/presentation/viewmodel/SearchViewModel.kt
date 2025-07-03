package com.mandarinkafe.mandarin.features.search.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.core.domain.models.extensions.isFavorite
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.search.domain.usecase.FilterUseCase
import com.mandarinkafe.mandarin.features.search.domain.usecase.GetFullMealListUseCase
import com.mandarinkafe.mandarin.features.search.domain.usecase.GetLabelsUseCase
import com.mandarinkafe.mandarin.features.search.presentation.SearchMapper.toUiModel
import com.mandarinkafe.mandarin.features.search.presentation.viewmodel.SearchContract.SearchEffect
import com.mandarinkafe.mandarin.features.search.presentation.viewmodel.SearchContract.SearchEvent
import com.mandarinkafe.mandarin.features.search.presentation.viewmodel.SearchContract.SearchState
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorOther
import com.mandarinkafe.mandarin.util.Resource.Idle
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.Resource.Success
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getLabelsUseCase: GetLabelsUseCase,
    private val getFullMealListUseCase: GetFullMealListUseCase,
    private val favoritesApi: FavoritesApi,
    private val filterUseCase: FilterUseCase
) : BaseViewModel<SearchEvent, SearchEffect, SearchState>() {
    override fun setInitialState() = SearchState()

    init {
        getLabels()
        loadMenu()
        observeFavorites()
    }

    override fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.ClearSearchInput -> clearSearchInput()
            is SearchEvent.SearchMealsByText -> searchByText(event.searchText)
            is SearchEvent.OnLabelClick -> setLabels(
                label = event.labelName,
                isChecked = event.isChecked
            )
        }
    }

    private fun getLabels() {
        viewModelScope.launch {
            val allLabels = getLabelsUseCase().map {
                it.toUiModel()
            }
            setState {
                copy(allLabels = allLabels)
            }
        }

    }

    private fun setLabels(label: String, isChecked: Boolean) {
        setState {
            val checkedLabels = checkedLabels.toMutableList()

            if (isChecked) {
                if (!checkedLabels.contains(label)) checkedLabels += label
            } else {
                checkedLabels.remove(label)
            }
            copy(
                checkedLabels = checkedLabels
            )
        }
        filterMenu()

    }

    // Очистить поле поиска
    private fun clearSearchInput() {
        setState {
            copy(
                filteredMealList = fullMealList.sortedWith(compareByDescending {
                    it.isFavorite(
                        favoritesIds
                    )
                }),
                latestSearchText = ""
            )
        }
        filterMenu()
    }

    // Поисковый запрос
    private fun searchByText(searchText: String) {
        setState { copy(latestSearchText = searchText) }
        filterMenu()
    }

    // Поиск по меню
    private fun filterMenu() {
        setState {
            val filtered = filterUseCase(
                fullMealList,
                latestSearchText,
                checkedLabels,
                state.value.favoritesIds
            )

            copy(filteredMealList = filtered)
        }
    }

    // Методы для загрузки меню
    private fun loadMenu() {
        viewModelScope.launch {
            getFullMealListUseCase().collectLatest { resource ->
                setLoading(resource is Loading)
                when (resource) {
                    is Success -> setState {
                        copy(
                            isLoading = false,
                            fullMealList = resource.data ?: emptyList(),
                            filteredMealList = resource.data?.sortedWith(compareByDescending {
                                it.isFavorite(
                                    favoritesIds
                                )
                            })
                                ?: emptyList()
                        )
                    }

                    is Loading -> {}
                    is Idle -> {}
                    else -> setError(resource)
                }
            }
        }
    }

    private fun setError(resource: Resource<*>) {
        val error = when (resource) {
            is Resource.ErrorEmptyData<*> -> UiError.MenuEmpty
            is Resource.ErrorNoInternet<*> -> UiError.NoInternet
            is ErrorOther<*> -> UiError.OtherError
            else -> return
        }
        setState { copy(error = error) }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesApi.observeFavoritesBaseMealIDs().collect { newFavorites ->
                setState {
                    copy(favoritesIds = newFavorites)
                }
            }
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }
}