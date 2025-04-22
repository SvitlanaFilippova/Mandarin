package com.mandarinkafe.mandarin.menu.ui.view_model

import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MenuItem
import com.mandarinkafe.mandarin.util.Constants.DEFAULT_UNSELECTED_INDEX
import com.mandarinkafe.mandarin.util.ui.BottomSheetEffect

sealed interface MenuContract {

    sealed interface Event {
        // Загрузка меню
        data object LoadMenu : Event
        data object ForceRefreshMenu : Event

        // Избранные
        data class ToggleFavorite(val meal: Meal) : Event

        // Скролл по меню
        data object ScrollToTop : Event
        data class ScrollToCategory(val newIndex: Int) : Event
        data class ScrollToSubCategory(val newIndex: Int) : Event
        data class BannerClick(val targetName: String) : Event

        // Корзина
        data class AddToCart(val meal: Meal) : Event
        data class RemoveFromCart(val meal: Meal) : Event
        data class OnMealCustomizationClick(val meal: Meal) : Event

        // Позвонить
        data object OnPhoneClick : Event

        // Поиск и фильтрация
        data class SearchMealsByText(val searchText: String) : Event
        data class SearchOnMealClick(val targetId: String) : Event
        data object SearchClearInput : Event
        data object SearchOnOpenSearchClick : Event
        data object OnOpenFavoritesClick : Event
        data object OnLabelsClick : Event

    }

    sealed interface Effect {
        data class ShowSnackbar(val message: String) : Effect
        data class OpenMealCustomization(val meal: Meal) : Effect, BottomSheetEffect
        data class OpenSearch(val focusSearch: Boolean) : Effect
        data object OpenFavorites : Effect
        data object CallPhone : Effect
    }

    data class State(
        val isLoading: Boolean = false,
        val menuItems: List<MenuItem> = emptyList(),
        val errorMessage: String? = null,
        val selectedTabIndex: Int = DEFAULT_UNSELECTED_INDEX,
        val selectedSubTabIndex: Int = DEFAULT_UNSELECTED_INDEX,
        val selectedMenuItemIndex: Int = DEFAULT_UNSELECTED_INDEX,
        val filteredMenuItems: List<MenuItem> = emptyList(),
        val latestSearchText: String = "",
    )
}