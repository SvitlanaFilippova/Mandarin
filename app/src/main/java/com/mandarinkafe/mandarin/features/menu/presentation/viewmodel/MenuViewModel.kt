package com.mandarinkafe.mandarin.features.menu.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.features.menu.domain.usecase.GetBannersUseCase
import com.mandarinkafe.mandarin.features.menu.domain.usecase.MenuInteractor
import com.mandarinkafe.mandarin.features.menu.presentation.mappers.MenuItemMapper.menuToMenuItems
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem
import com.mandarinkafe.mandarin.features.menu.presentation.models.extensions.getName
import com.mandarinkafe.mandarin.features.menu.presentation.viewmodel.MenuContract.MenuEffect
import com.mandarinkafe.mandarin.features.menu.presentation.viewmodel.MenuContract.MenuEffect.OpenSearch
import com.mandarinkafe.mandarin.features.menu.presentation.viewmodel.MenuContract.MenuEvent
import com.mandarinkafe.mandarin.features.menu.presentation.viewmodel.MenuContract.MenuState
import com.mandarinkafe.mandarin.util.Constants.DEFAULT_UNSELECTED_INDEX
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
class MenuViewModel @Inject constructor(
    private val menuInteractor: MenuInteractor,
    private val favoritesApi: FavoritesApi,
    private val getBannersUseCase: GetBannersUseCase
) : BaseViewModel<MenuEvent, MenuEffect, MenuState>() {
    override fun setInitialState() = MenuState()

    init {
        loadMenu()
        getBanners()
        observeFavorites()
    }

    override fun onEvent(event: MenuEvent) {
        when (event) {
            is MenuEvent.ScrollToCategory -> scrollToCategory(event.newIndex)
            is MenuEvent.ScrollToSubCategory -> scrollToSubCategory(event.newIndex)
            is MenuEvent.ScrollToTop -> scrollToTop()
            is MenuEvent.BannerClick -> findMenuItemByBanner(event.banner)
            is MenuEvent.ResetSelectedMenuItemIndex -> resetSelectedMenuItemIndex()
            is MenuEvent.SearchOnOpenSearchClick -> sendEffect(OpenSearch(focusSearch = true))

        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }

    // Методы для загрузки меню
    private fun loadMenu() {
        viewModelScope.launch {
            menuInteractor.getMenu().collectLatest { resource ->
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

    private fun observeFavorites() {
        viewModelScope.launch {
            val favoriteIdsFromStorage = mutableSetOf<String>()
            favoritesApi.observeFavoritesBaseMealIDs().collect { favoriteIdsFromStorage.addAll(it) }
            setState {
                copy(
                    favoriteIds = favoriteIdsFromStorage
                )
            }
        }
    }

    private fun setData(data: List<MealCategory>?) {
        if (!data.isNullOrEmpty()) {
            setState {
                copy(
                    menuItems = menuToMenuItems(data),
                    error = null
                )
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