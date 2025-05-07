package com.mandarinkafe.mandarin.search.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.favorites.data.mapper.FavoriteMapper.toFavoriteMeal
import com.mandarinkafe.mandarin.favorites.domain.usecase.FavoritesInteractor
import com.mandarinkafe.mandarin.menu.domain.models.MenuItem
import com.mandarinkafe.mandarin.menu.domain.usecase.MenuInteractor
import com.mandarinkafe.mandarin.search.domain.usecase.GetLabelsUseCase
import com.mandarinkafe.mandarin.search.ui.view_model.SearchContract.Effect
import com.mandarinkafe.mandarin.search.ui.view_model.SearchContract.Effect.OpenMealDetailsBS
import com.mandarinkafe.mandarin.search.ui.view_model.SearchContract.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getLabelsUseCase: GetLabelsUseCase,
    private val favoritesInteractor: FavoritesInteractor,
    private val menuInteractor: MenuInteractor,
) : ViewModel() {
    private val _state = MutableStateFlow(SearchContract.State())
    val state: StateFlow<SearchContract.State> = _state.asStateFlow()

    private val _effect =
        MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    init {
        getLabels()
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.ClearSearchInput -> clearSearchInput()
            is Event.SearchMealsByText -> filterMenu(event.searchText)
            is Event.ToggleFavorite -> toggleFavorite(event.meal)
            is Event.UpdateMealFavorite -> updateMealFavorite(
                id = event.id,
                isFavorite = event.isFavorite
            )

            is Event.OnLabelClick -> changeAdds(
                label = event.labelName,
                isChecked = event.isChecked
            )

            is Event.OnMealDetailsClick -> sendEffect(
                OpenMealDetailsBS(meal = event.meal)
            )
        }
    }

    private fun getLabels() {
        viewModelScope.launch {
            _state.update {
                it.copy(allLabels = getLabelsUseCase.execute().map { it.name })
            }
        }

    }

    private fun changeAdds(label: String, isChecked: Boolean) {
        _state.update { currentState ->
            val checkedLabels = currentState.checkedLabels.toMutableList()

            if (isChecked) {
                if (!checkedLabels.contains(label)) checkedLabels += label
            } else {
                checkedLabels.remove(label)
            }

            currentState.copy(
                checkedLabels = checkedLabels
            )

        }
    }

    // Очистить поле поиска
    private fun clearSearchInput() {
        _state.update { it.copy(filteredMenuItems = emptyList(), latestSearchText = "") }
    }

    // Поиск по меню
    private fun filterMenu(searchText: String? = null) {
        if (!searchText.isNullOrEmpty()) {
            val filteredMenuItems = _state.value.menuItems.filter {
                it is MenuItem.MealItem && it.meal.name.contains(searchText, ignoreCase = true)
            }
                .sortedWith( // Дополнительная сортировка, чтобы в начале от ображались избранные блюда
                    compareByDescending<MenuItem> {
                        (it as MenuItem.MealItem).meal.isFavorite
                    }
                )
            _state.update {
                it.copy(
                    filteredMenuItems = filteredMenuItems,
                    latestSearchText = searchText
                )
            }
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

            _state.update { state ->
                val updatedMenuItems =
                    updateMealItemInList(state.menuItems, meal.id, isNowFavorite)
                val updatedFiltered = if (state.filteredMenuItems.isNotEmpty()) {
                    updateMealItemInList(state.filteredMenuItems, meal.id, isNowFavorite)
                } else {
                    state.filteredMenuItems
                }

                state.copy(
                    menuItems = updatedMenuItems,
                    filteredMenuItems = updatedFiltered
                )
            }
        }
    }

    // Если состояние избранного менялось в другом месте (например,в BottomSheet)
    private fun updateMealFavorite(id: String, isFavorite: Boolean) {
        _state.update { currentState ->
            val updatedMenuItems = currentState.menuItems.map { item ->
                if (item is MenuItem.MealItem && item.meal.id == id) {
                    item.copy(meal = item.meal.copy(isFavorite = isFavorite))
                } else item
            }
            currentState.copy(
                menuItems = updatedMenuItems,
            )
        }
    }

    private fun updateMealItemInList(
        list: List<MenuItem>,
        mealId: String,
        isFavorite: Boolean
    ): List<MenuItem> {
        val index = list.indexOfFirst {
            it is MenuItem.MealItem && it.meal.id == mealId
        }
        if (index == -1) return list

        val updatedList = list.toMutableList()
        val mealItem = updatedList[index] as MenuItem.MealItem
        updatedList[index] = mealItem.copy(
            meal = mealItem.meal.copy(isFavorite = isFavorite)
        )
        return updatedList
    }

    private fun sendEffect(effect: Effect) {
        viewModelScope.launch { _effect.emit(effect) }
    }

}