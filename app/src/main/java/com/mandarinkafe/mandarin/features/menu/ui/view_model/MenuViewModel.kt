package com.mandarinkafe.mandarin.features.menu.ui.view_model

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.BaseViewModel
import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.features.menu.domain.usecase.GetBannersUseCase
import com.mandarinkafe.mandarin.features.menu.domain.usecase.MenuInteractor
import com.mandarinkafe.mandarin.features.menu.ui.mappers.MenuItemMapper.menuToMenuItems
import com.mandarinkafe.mandarin.features.menu.ui.models.MenuItem
import com.mandarinkafe.mandarin.features.menu.ui.models.extensions.getName
import com.mandarinkafe.mandarin.features.menu.ui.models.extensions.updateMeal
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.MenuEffect
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.MenuEffect.CallPhone
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.MenuEffect.OpenMealDetailsBS
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.MenuEffect.OpenSearch
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.MenuEvent
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.MenuState
import com.mandarinkafe.mandarin.util.Constants.DEFAULT_UNSELECTED_INDEX
import com.mandarinkafe.mandarin.util.Resource.Error
import com.mandarinkafe.mandarin.util.Resource.Idle
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.Resource.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuInteractor: MenuInteractor,
    private val favoritesApi: FavoritesApi,
    private val getBannersUseCase: GetBannersUseCase
) : BaseViewModel<MenuEvent, MenuEffect, MenuState>() {
    override fun setInitialState() = MenuState()

    init {
        loadMenu()
        getBanners()
    }

    override fun onEvent(event: MenuEvent) {
        when (event) {
            is MenuEvent.OnPhoneClick -> sendEffect(CallPhone)
            is MenuEvent.ToggleFavorite -> toggleFavorite(event.meal)
            is MenuEvent.ScrollToCategory -> scrollToCategory(event.newIndex)
            is MenuEvent.ScrollToSubCategory -> scrollToSubCategory(event.newIndex)
            is MenuEvent.ScrollToTop -> scrollToTop()
            is MenuEvent.BannerClick -> findMenuItemByBanner(event.banner)
            is MenuEvent.ResetSelectedMenuItemIndex -> resetSelectedMenuItemIndex()
            is MenuEvent.SearchOnOpenSearchClick -> sendEffect(OpenSearch(focusSearch = true))
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
        viewModelScope.launch {
            menuInteractor.getMenu().collectLatest { resource ->
                setLoading(resource is Loading)
                when (resource) {
                    is Success -> setData(resource.data)
                    is Error -> setError(resource.message)
                    is Loading -> {}
                    is Idle -> {}
                }
            }
        }
    }

    private fun setData(data: List<MealCategory>?) {
        if (!data.isNullOrEmpty()) {
            setState {
                copy(
                    menuItems = menuToMenuItems(data),
                    errorMessage = null
                )
            }
        }
    }

    private fun setError(errorMessage: String?) {
        setState { copy(errorMessage = errorMessage) }
    }

    private fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
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
    private fun findMenuItemByBanner(banner: Banner) {
        // Если нет цели — сбрасываем и выходим
        if (banner.targetName.isBlank()) {
            resetSelectedMenuItemIndex()
            return
        }
        viewModelScope.launch {
            val menuItems = state.value.menuItems
            val targetIndex = findMenuItemIndex(banner, menuItems)
            setState { copy(selectedMenuItemIndex = targetIndex) }
        }
    }

    private fun findMenuItemIndex(banner: Banner, menuItems: List<MenuItem>): Int {
        val name = banner.targetName.trim()

        // Поиск по имени (точное совпадение)
        menuItems.indexOfFirst { it.getName()?.equals(name, ignoreCase = true) == true }
            .takeIf { it >= 0 }?.let { return it }

        // Поиск по имени (частичное совпадение)
        menuItems.indexOfFirst { it.getName()?.contains(name, ignoreCase = true) == true }
            .takeIf { it >= 0 }?.let { return it }

        // По умолчанию
        return DEFAULT_UNSELECTED_INDEX
    }

    private fun resetSelectedMenuItemIndex() {
        setState { copy(selectedMenuItemIndex = DEFAULT_UNSELECTED_INDEX) }

    }

    // Добавить блюдо в избранное или удалить
    private fun toggleFavorite(meal: Meal) {
        viewModelScope.launch {
            val isNowFavorite = if (meal.isFavorite) {
                favoritesApi.removeFavorite(meal.id)
                false
            } else {
                favoritesApi.addFavorite(meal.id)
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

    private fun getBanners() {
        viewModelScope.launch {
            setState { copy(bannersAreLoading = true) }
            val result = getBannersUseCase()
            if (result is Success) {
                val banners = result.data ?: emptyList()
                setState { copy(banners = banners, bannersAreLoading = false) }
            }
        }
    }

}