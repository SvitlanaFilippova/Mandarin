package com.mandarinkafe.mandarin.menu.ui

import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import com.mandarinkafe.mandarin.util.Constants.DEFAULT_UNSELECTED_INDEX

sealed interface MenuContract {

    sealed interface Event {
        data object LoadMenu : Event
        data class ToggleFavorite(val meal: Meal) : Event
        data class ScrollToCategory(val newIndex: Int) : Event
        data class ScrollToSubCategory(val newIndex: Int) : Event
        data class ScrollToMeal(val meal: Meal) : Event
        data class BannerClick(val targetName: String) : Event
        data class AddToCart(val meal: Meal) : Event
        data class RemoveFromCart(val meal: Meal) : Event
        data class OpenMealCustomization(val meal: Meal) : Event
        data class SearchMealsByText(val searchText: String) : Event
        data object ClearSearchInput : Event
    }

    sealed interface Effect {
        data class ShowSnackbar(val message: String) : Effect
        data class OpenMealCustomization(val meal: Meal) : Effect
    }

    data class State(
        val isLoading: Boolean = false,
        val menuItems: List<MenuRVItem> = emptyList(),
        val errorMessage: String? = null,
        val selectedTabIndex: Int = DEFAULT_UNSELECTED_INDEX,
        val selectedSubTabIndex: Int = DEFAULT_UNSELECTED_INDEX,
        val selectedBannerIndex: Int = DEFAULT_UNSELECTED_INDEX,
        val filteredMenuItems: List<MenuRVItem> = emptyList(),
        val latestSearchText: String = "",
    )
}