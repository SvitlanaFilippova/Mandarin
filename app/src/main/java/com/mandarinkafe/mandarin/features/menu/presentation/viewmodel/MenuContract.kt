package com.mandarinkafe.mandarin.features.menu.presentation.viewmodel

import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState
import com.mandarinkafe.mandarin.util.Constants.DEFAULT_UNSELECTED_INDEX

sealed interface MenuContract {

    sealed interface MenuEvent : BaseEvent {
        // Скроллинг
        data object ScrollToTop : MenuEvent
        data class ScrollToCategory(val newIndex: Int) : MenuEvent
        data class ScrollToSubCategory(val newIndex: Int) : MenuEvent

        // Действия с баннерами
        data class BannerClick(val banner: Banner) : MenuEvent

        // Сброс состояния
        data object ResetSelectedMenuItemIndex : MenuEvent

        // Обновление данных
        data object ForceRefresh : MenuEvent

        // Поиск
        data object SearchOnOpenSearchClick : MenuEvent
    }

    sealed interface MenuEffect : BaseEffect {
        data class OpenSearch(val focusSearch: Boolean) : MenuEffect
    }

    data class MenuState(
        val isLoading: Boolean = false,
        val menuItems: List<MenuItem> = emptyList(),
        val favoriteIds: Set<String> = emptySet(),
        val error: UiError? = null,
        val banners: List<Banner> = emptyList(),
        val bannersAreLoading: Boolean = false,
        val selectedTabIndex: Int = DEFAULT_UNSELECTED_INDEX,
        val selectedSubTabIndex: Int = DEFAULT_UNSELECTED_INDEX,
        val selectedMenuItemIndex: Int = DEFAULT_UNSELECTED_INDEX,
    ) : BaseState
}