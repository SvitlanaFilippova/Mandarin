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
        data class BannerClick(val targetName: String) : Event
        data class AddToCart(val meal: Meal) : Event
        data class RemoveFromCart(val meal: Meal) : Event

    }

    sealed interface Effect {
        data class ShowSnackbar(val message: String) : Effect
        data class NavigateTo(val route: String) : Effect
    }

    data class State(
        val isLoading: Boolean = false,
        val menuItems: List<MenuRVItem> = emptyList(),
        val errorMessage: String? = null,
        val selectedTabIndex: Int = DEFAULT_UNSELECTED_INDEX,
        val selectedSubTabIndex: Int = DEFAULT_UNSELECTED_INDEX,
        val selectedBannerIndex: Int = DEFAULT_UNSELECTED_INDEX
    )
}