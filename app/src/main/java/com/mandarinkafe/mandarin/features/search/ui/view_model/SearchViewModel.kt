package com.mandarinkafe.mandarin.features.search.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toFavoriteMeal
import com.mandarinkafe.mandarin.features.favorites.domain.usecase.FavoritesInteractor
import com.mandarinkafe.mandarin.features.menu.domain.models.MenuItem
import com.mandarinkafe.mandarin.features.menu.domain.usecase.MenuInteractor
import com.mandarinkafe.mandarin.features.search.SearchMapper.toUiModel
import com.mandarinkafe.mandarin.features.search.domain.usecase.GetLabelsUseCase
import com.mandarinkafe.mandarin.features.search.ui.view_model.SearchContract.Effect
import com.mandarinkafe.mandarin.features.search.ui.view_model.SearchContract.Effect.OpenMealDetailsBS
import com.mandarinkafe.mandarin.features.search.ui.view_model.SearchContract.Event
import com.mandarinkafe.mandarin.util.Constants.DELAY_BEFORE_NEXT_ATTEMPT
import com.mandarinkafe.mandarin.util.Constants.MAX_ATTEMPTS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
        loadMenu()
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
                it.copy(allLabels = getLabelsUseCase.execute().map { it.toUiModel() })
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
        filterMenu(_state.value.latestSearchText)
    }

    // Очистить поле поиска
    private fun clearSearchInput() {
        _state.update { it.copy(filteredMenuItems = emptyList(), latestSearchText = "") }
    }

    // Поиск по меню
    private fun filterMenu(searchText: String? = null) {
        val menuItems = _state.value.menuItems
        val checkedLabels = _state.value.checkedLabels

        val filtered = if (searchText.isNullOrBlank() && checkedLabels.isEmpty()) {
            // Нет активных фильтров — показываем всё
            menuItems
        } else {
            menuItems.filter { item ->
                if (item !is MenuItem.MealItem) return@filter false

                val meal = item.meal

                val matchesSearch = searchText.isNullOrBlank() ||
                        meal.name.contains(searchText, ignoreCase = true)

                val mealLabelNames = meal.labels.map { it.name }
                val matchesLabels =
                    checkedLabels.isEmpty() || checkedLabels.all { it in mealLabelNames }

                matchesSearch && matchesLabels
            }
        }.sortedWith(compareByDescending<MenuItem> {
            (it as? MenuItem.MealItem)?.meal?.isFavorite == true
        })

        _state.update {
            it.copy(
                filteredMenuItems = filtered,
                latestSearchText = searchText.orEmpty()
            )
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

    // Метод для загрузки меню
    private fun loadMenu() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            var attempts = 0
            var success = false

            // Попытки до максимума
            while (attempts < MAX_ATTEMPTS) {
                menuInteractor.getMenu()
                    .collect { (menu, errorMessage) ->
                        // Если меню в процессе загрузки, пробуем снова
                        if (menu == null && errorMessage == null) {
                            attempts++
                            delay(DELAY_BEFORE_NEXT_ATTEMPT) // Задержка перед повторной попыткой
                        } else {
                            // Обработка успешной загрузки данных
                            if (!menu.isNullOrEmpty()) {
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        menuItems = menu,
                                        filteredMenuItems = menu.sortedWith(compareByDescending<MenuItem> {
                                            (it as? MenuItem.MealItem)?.meal?.isFavorite
                                        })
                                    )
                                }
                                success = true
                            } else {
                                // Обработка ошибки
                                _state.update {
                                    it.copy(
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
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Не удалось загрузить меню. Попробуйте позже."
                    )
                }
            }
        }
    }
}