package com.mandarinkafe.mandarin.features.search.ui.view_model

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.BaseViewModel
import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.ui.models.UiError
import com.mandarinkafe.mandarin.features.search.SearchMapper.toUiModel
import com.mandarinkafe.mandarin.features.search.domain.usecase.FilterUseCase
import com.mandarinkafe.mandarin.features.search.domain.usecase.GetFullMealListUseCase
import com.mandarinkafe.mandarin.features.search.domain.usecase.GetLabelsUseCase
import com.mandarinkafe.mandarin.features.search.ui.view_model.SearchContract.SearchEffect
import com.mandarinkafe.mandarin.features.search.ui.view_model.SearchContract.SearchEffect.OpenMealDetailsBS
import com.mandarinkafe.mandarin.features.search.ui.view_model.SearchContract.SearchEvent
import com.mandarinkafe.mandarin.features.search.ui.view_model.SearchContract.SearchState
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorOther
import com.mandarinkafe.mandarin.util.Resource.Idle
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.Resource.Success
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
    }

    override fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.ClearSearchInput -> clearSearchInput()
            is SearchEvent.SearchMealsByText -> searchByText(event.searchText)
            is SearchEvent.ToggleFavorite -> toggleFavorite(event.meal)
            is SearchEvent.UpdateMealFavorite -> updateMealFavorite(
                id = event.id,
                isFavorite = event.isFavorite
            )

            is SearchEvent.OnLabelClick -> setLabels(
                label = event.labelName,
                isChecked = event.isChecked
            )

            is SearchEvent.OnMealDetailsClick -> sendEffect(
                OpenMealDetailsBS(meal = event.meal)
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
                filteredMealList = fullMealList.sortedWith(compareByDescending { it.isFavorite }),
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
                checkedLabels
            )

            copy(filteredMealList = filtered)
        }
    }

    // Добавить блюдо в избранное или удалить
    private fun toggleFavorite(meal: Meal) {
        viewModelScope.launch {
            val isNowFavorite = favoritesApi.toggleFavorite(meal)
            setState {
                val updatedFullList = fullMealList.map { currentMeal ->
                    if (currentMeal.id == meal.id) {
                        currentMeal.copy(isFavorite = isNowFavorite)
                    } else {
                        currentMeal
                    }
                }

                val updatedFilteredList = filteredMealList.map { currentMeal ->
                    if (currentMeal.id == meal.id) {
                        currentMeal.copy(isFavorite = isNowFavorite)
                    } else {
                        currentMeal
                    }
                }

                copy(
                    fullMealList = updatedFullList,
                    filteredMealList = updatedFilteredList
                )
            }
        }
    }

    // Если состояние избранного менялось в другом месте (например,в BottomSheet)
    private fun updateMealFavorite(id: String, isFavorite: Boolean) {
        setState {
            val updatedFullList = fullMealList.map { meal ->
                if (meal.id == id) meal.copy(isFavorite = isFavorite) else meal
            }
            val updatedFilteredList = filteredMealList.map { meal ->
                if (meal.id == id) meal.copy(isFavorite = isFavorite) else meal
            }

            copy(
                fullMealList = updatedFullList,
                filteredMealList = updatedFilteredList
            )
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
                            filteredMealList = resource.data?.sortedWith(compareByDescending { it.isFavorite })
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

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }
}