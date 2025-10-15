package com.mandarinkafe.mandarin.features.menu.presentation.viewmodel

import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem
import com.mandarinkafe.mandarin.util.presentation.BaseContract
import com.mandarinkafe.mandarin.util.Constants.DEFAULT_UNSELECTED_INDEX

sealed interface MenuContract {

    sealed interface MenuEvent : BaseContract.BaseEvent {
        // Действия с баннерами
        data class BannerClick(val banner: Banner) : MenuEvent

        // Сброс состояния
        data object ResetSelectedMenuItemIndex : MenuEvent

        // Обновление данных
        data object ForceRefresh : MenuEvent

        // Поиск
        data object SearchOnOpenSearchClick : MenuEvent
    }

    sealed interface MenuEffect : BaseContract.BaseEffect {
        data class OpenSearch(val focusSearch: Boolean) : MenuEffect
    }

    data class MenuState(
        val isLoading: Boolean = false,
        val menuItems: List<MenuItem> = emptyList(),
        val favoriteIds: Set<String> = emptySet(),
        val error: UiError? = null,
        val banners: List<Banner> = emptyList(),
        val bannersAreLoading: Boolean = false,
        val selectedMenuItemIndex: Int = DEFAULT_UNSELECTED_INDEX,
    ) : BaseContract.BaseState
}




