package com.mandarinkafe.mandarin.menu.ui

import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.util.RVItem

sealed interface MenuContract {

    sealed interface Event {
        data object LoadMenu : Event
        data class ToggleFavorite(val meal: Meal) : Event
        data class ScrollToCategory(val categoryId: String) : Event
        data class ScrollToSubCategory(val categoryId: String) : Event
        data class AddToCart(val meal: Meal) : Event
        data class RemoveFromCart(val meal: Meal) : Event
    }

    sealed interface Effect {
        data class ShowSnackbar(val message: String) : Effect
        data class NavigateTo(val route: String) : Effect
    }

    data class State(
        val isLoading: Boolean = false,
        val menuItems: List<RVItem> = emptyList(),
        val errorMessage: String? = null,
        val selectedTabIndex: Int = 0,
        val selectedSubTabIndex: Int = -1
    )
}