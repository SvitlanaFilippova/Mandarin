package com.mandarinkafe.mandarin.features.search.ui.view_model

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.BaseViewModel
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toFavoriteMeal
import com.mandarinkafe.mandarin.features.favorites.domain.usecase.FavoritesInteractor
import com.mandarinkafe.mandarin.features.search.SearchMapper.toUiModel
import com.mandarinkafe.mandarin.features.search.domain.usecase.FilterUseCase
import com.mandarinkafe.mandarin.features.search.domain.usecase.GetFullMealListUseCase
import com.mandarinkafe.mandarin.features.search.domain.usecase.GetLabelsUseCase
import com.mandarinkafe.mandarin.features.search.ui.view_model.SearchContract.SearchEffect
import com.mandarinkafe.mandarin.features.search.ui.view_model.SearchContract.SearchEffect.OpenMealDetailsBS
import com.mandarinkafe.mandarin.features.search.ui.view_model.SearchContract.SearchEvent
import com.mandarinkafe.mandarin.features.search.ui.view_model.SearchContract.SearchState
import com.mandarinkafe.mandarin.util.Constants.DELAY_BEFORE_NEXT_ATTEMPT
import com.mandarinkafe.mandarin.util.Constants.MAX_ATTEMPTS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getLabelsUseCase: GetLabelsUseCase,
    private val getFullMealListUseCase: GetFullMealListUseCase,
    private val favoritesInteractor: FavoritesInteractor,
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
            val isNowFavorite = if (meal.isFavorite) {
                favoritesInteractor.removeFromFavorites(meal.toFavoriteMeal())
                false
            } else {
                favoritesInteractor.addToFavorites(meal.toFavoriteMeal())
                true
            }

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

    // Метод для загрузки меню
    private fun loadMenu() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            var attempts = 0
            var success = false

            // Попытки до максимума
            while (attempts < MAX_ATTEMPTS) {
                getFullMealListUseCase()
                    .collect { (menu, errorMessage) ->
                        // Если меню в процессе загрузки, пробуем снова
                        if (menu == null && errorMessage == null) {
                            attempts++
                            delay(DELAY_BEFORE_NEXT_ATTEMPT) // Задержка перед повторной попыткой
                        } else {
                            // Обработка успешной загрузки данных
                            if (!menu.isNullOrEmpty()) {
                                setState {
                                    copy(
                                        isLoading = false,
                                        fullMealList = menu,
                                        filteredMealList = menu.sortedWith(compareByDescending { it.isFavorite })
                                    )
                                }
                                success = true
                            } else {
                                // Обработка ошибки
                                setState {
                                    copy(
                                        isLoading = false,
                                        errorMessage = errorMessage
                                    )
                                }
                            }
                            return@collect // Завершаем коллекцию данных после успешной обработки
                        }
                    }
            }
            // Если после всех попыток данных нет, устанавливаем ошибку
            if (!success) {
                setState {
                    copy(
                        isLoading = false,
                        errorMessage = "Не удалось загрузить меню. Попробуйте позже."
                    )
                }
            }
        }
    }
}