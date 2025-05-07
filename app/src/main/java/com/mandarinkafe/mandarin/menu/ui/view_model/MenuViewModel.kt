package com.mandarinkafe.mandarin.menu.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.favorites.data.mapper.FavoriteMapper.toFavoriteMeal
import com.mandarinkafe.mandarin.favorites.data.mapper.FavoriteMapper.toMealItem
import com.mandarinkafe.mandarin.favorites.domain.usecase.FavoritesInteractor
import com.mandarinkafe.mandarin.menu.domain.models.MenuItem
import com.mandarinkafe.mandarin.menu.domain.models.getName
import com.mandarinkafe.mandarin.menu.domain.usecase.MenuInteractor
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Effect
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Effect.CallPhone
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Effect.OpenFavorites
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Effect.OpenMealDetailsBS
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Effect.OpenSearch
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Event
import com.mandarinkafe.mandarin.util.Constants.DEFAULT_UNSELECTED_INDEX
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
class MenuViewModel @Inject constructor(
    private val menuInteractor: MenuInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _state = MutableStateFlow(MenuContract.State()) // для хранения состояния ЮИ
    val state: StateFlow<MenuContract.State> = _state.asStateFlow()

    private val _effect =
        MutableSharedFlow<Effect>() // для одноразовых событий. Например, показа снекбар
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    init {
        onEvent(Event.LoadMenu)
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.LoadMenu -> loadMenu()
            is Event.ForceRefreshMenu -> forceRefreshMenu()
            is Event.OnPhoneClick -> sendEffect(CallPhone)
            is Event.ToggleFavorite -> toggleFavorite(event.meal)
            is Event.ScrollToCategory -> scrollToCategory(event.newIndex)
            is Event.ScrollToSubCategory -> scrollToSubCategory(event.newIndex)
            is Event.ScrollToTop -> scrollToTop()
            is Event.BannerClick -> findMenuItemIndexByName(event.targetName)
            is Event.SearchOnOpenSearchClick -> sendEffect(OpenSearch(focusSearch = true))
            is Event.OnOpenFavoritesClick -> sendEffect(OpenFavorites)
            is Event.OnLabelsClick -> sendEffect(OpenSearch(focusSearch = false))
            is Event.UpdateMealFavorite -> updateMealFavorite(
                id = event.id,
                isFavorite = event.isFavorite
            )

            is Event.OnMealDetailsClick -> sendEffect(
                OpenMealDetailsBS(meal = event.meal)
            )
        }
    }

    private fun sendEffect(effect: Effect) {
        viewModelScope.launch { _effect.emit(effect) }
    }

    // Методы для загрузки меню
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
                                    it.copy(isLoading = false, menuItems = menu)
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

    private fun forceRefreshMenu() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            menuInteractor.forceRefresh()
            loadMenu()
        }
    }

    // Методы управлением скроллом
    private fun scrollToCategory(newIndex: Int) {
        if (newIndex >= 0) {
            _state.update {
                it.copy(
                    selectedTabIndex = newIndex,
                    selectedSubTabIndex = DEFAULT_UNSELECTED_INDEX
                )
            }
        }
    }

    private fun scrollToSubCategory(newIndex: Int) {
        if (newIndex >= 0) {
            _state.update { it.copy(selectedSubTabIndex = newIndex) }
        }
    }

    private fun scrollToTop() {
        _state.update {
            it.copy(
                selectedTabIndex = DEFAULT_UNSELECTED_INDEX,
                selectedSubTabIndex = DEFAULT_UNSELECTED_INDEX
            )
        }
    }

    // Обработка кликов по баннерам - поиск подходящей категории/блюда в меню и скролл к нему
    private fun findMenuItemIndexByName(targetName: String) {
        viewModelScope.launch {
            var menuItems = state.value.menuItems

            // Ищем сначала точное совпадение, затем частичное
            val targetIndex = menuItems
                .indexOfFirst { item ->
                    item.getName()?.equals(targetName, ignoreCase = true) == true
                }
                .takeIf { it >= 0 }
                ?: menuItems.indexOfFirst { item ->
                    item.getName()?.contains(targetName, ignoreCase = true) == true
                }
                    .takeIf { it >= 0 }
                ?: 0

            _state.update { it.copy(selectedMenuItemIndex = targetIndex) }
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
                state.copy(
                    menuItems = updatedMenuItems,
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

    // Получить список избранных блюд
    fun getFavorites(): List<MenuItem> {
        return favoritesInteractor.getFavorites().map { it.toMealItem() }
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
}