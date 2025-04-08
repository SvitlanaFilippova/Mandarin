package com.mandarinkafe.mandarin.search.ui.view_model

import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem

sealed interface SearchContract {

    sealed interface Event {
        data class SearchMealsByText(val searchText: String) : Event
        data object ClearSearchInput : Event
    }

    sealed interface Effect {
        data class GoToMenu(val meal: Meal) : Effect
    }

    data class State(
        val isLoading: Boolean = false,
        val menuItems: List<MenuRVItem> = emptyList(),
        val filteredMenuItems: List<MenuRVItem> = emptyList(),
        val latestSearchText: String = "",
    )
}