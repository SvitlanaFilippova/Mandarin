package com.mandarinkafe.mandarin.menu.ui

import com.mandarinkafe.mandarin.util.RVItem

sealed interface MenuViewState {
    data object Loading : MenuViewState
    data class Content(
        val menuItems: List<RVItem>,
        val selectedTabIndex: Int = 0,
        val selectedSubTabIndex: Int = -1
    ) : MenuViewState

    data object Error : MenuViewState
}