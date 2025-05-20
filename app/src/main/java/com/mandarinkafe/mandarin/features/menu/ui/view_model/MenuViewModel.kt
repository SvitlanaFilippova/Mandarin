package com.mandarinkafe.mandarin.features.menu.ui.view_model

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.BaseViewModel
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toFavoriteMeal
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toMealItem
import com.mandarinkafe.mandarin.features.favorites.domain.usecase.FavoritesInteractor
import com.mandarinkafe.mandarin.features.menu.domain.usecase.MenuInteractor
import com.mandarinkafe.mandarin.features.menu.ui.mappers.MenuItemMapper.menuToMenuItems
import com.mandarinkafe.mandarin.features.menu.ui.models.MenuItem
import com.mandarinkafe.mandarin.features.menu.ui.models.getName
import com.mandarinkafe.mandarin.features.menu.ui.models.updateMeal
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.MenuEffect
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.MenuEffect.CallPhone
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.MenuEffect.OpenFavorites
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.MenuEffect.OpenMealDetailsBS
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.MenuEffect.OpenSearch
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.MenuEvent
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.MenuState
import com.mandarinkafe.mandarin.util.Constants.DEFAULT_UNSELECTED_INDEX
import com.mandarinkafe.mandarin.util.Constants.DELAY_BEFORE_NEXT_ATTEMPT
import com.mandarinkafe.mandarin.util.Constants.MAX_ATTEMPTS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuInteractor: MenuInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : BaseViewModel<MenuEvent, MenuEffect, MenuState>() {
    override fun setInitialState() = MenuState()

    init {
        onEvent(MenuEvent.LoadMenu)
    }

    override fun onEvent(event: MenuEvent) {
        when (event) {
            is MenuEvent.LoadMenu -> loadMenu()
            is MenuEvent.ForceRefreshMenu -> forceRefreshMenu()
            is MenuEvent.OnPhoneClick -> sendEffect(CallPhone)
            is MenuEvent.ToggleFavorite -> toggleFavorite(event.meal)
            is MenuEvent.ScrollToCategory -> scrollToCategory(event.newIndex)
            is MenuEvent.ScrollToSubCategory -> scrollToSubCategory(event.newIndex)
            is MenuEvent.ScrollToTop -> scrollToTop()
            is MenuEvent.BannerClick -> findMenuItemIndexByName(event.targetName)
            is MenuEvent.ResetSelectedMenuItemIndex -> resetSelectedMenuItemIndex()
            is MenuEvent.SearchOnOpenSearchClick -> sendEffect(OpenSearch(focusSearch = true))
            is MenuEvent.OnOpenFavoritesClick -> sendEffect(OpenFavorites)
            is MenuEvent.OnLabelsClick -> sendEffect(OpenSearch(focusSearch = false))
            is MenuEvent.UpdateMealFavorite -> updateMealFavorite(
                id = event.id,
                isFavorite = event.isFavorite
            )

            is MenuEvent.OnMealDetailsClick -> sendEffect(
                OpenMealDetailsBS(meal = event.meal)
            )

        }
    }

    // Методы для загрузки меню
    private fun loadMenu() {
        setState { copy(isLoading = true) }
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
                                setState {
                                    copy(isLoading = false, menuItems = menuToMenuItems(menu))
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

    private fun forceRefreshMenu() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            menuInteractor.forceRefresh()
            loadMenu()
        }
    }

    // Методы управлением скроллом
    private fun scrollToCategory(newIndex: Int) {
        if (newIndex >= 0) {
            setState {
                copy(
                    selectedTabIndex = newIndex,
                    selectedSubTabIndex = DEFAULT_UNSELECTED_INDEX
                )
            }
        }
    }

    private fun scrollToSubCategory(newIndex: Int) {
        if (newIndex >= 0) {
            setState { copy(selectedSubTabIndex = newIndex) }
        }
    }

    private fun scrollToTop() {
        setState {
            copy(
                selectedTabIndex = DEFAULT_UNSELECTED_INDEX,
                selectedSubTabIndex = DEFAULT_UNSELECTED_INDEX,
                selectedMenuItemIndex = DEFAULT_UNSELECTED_INDEX
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

            setState { copy(selectedMenuItemIndex = targetIndex) }
        }
    }

    private fun resetSelectedMenuItemIndex() {
        setState { copy(selectedMenuItemIndex = DEFAULT_UNSELECTED_INDEX) }

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
                copy(
                    menuItems = menuItems.updateMeal(meal.id) { meal ->
                        meal.copy(isFavorite = isNowFavorite)
                    }
                )
            }
        }
    }

    // Если состояние избранного менялось в другом месте (например,в BottomSheet)
    private fun updateMealFavorite(id: String, isFavorite: Boolean) {
        setState {
            copy(
                menuItems = menuItems.updateMeal(id) { meal ->
                    meal.copy(isFavorite = isFavorite)
                }
            )
        }
    }

    // Получить список избранных блюд
    fun getFavorites(): List<MenuItem> {
        return favoritesInteractor.getFavorites().map { it.toMealItem() }
    }

}