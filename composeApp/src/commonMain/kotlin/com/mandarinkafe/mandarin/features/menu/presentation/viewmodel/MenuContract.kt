package com.mandarinkafe.mandarin.features.menu.presentation.viewmodel

import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem
import com.mandarinkafe.mandarin.features.menu.presentation.models.OrderClosingBannerUi
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import com.mandarinkafe.mandarin.util.Constants.DEFAULT_UNSELECTED_INDEX
import com.mandarinkafe.mandarin.util.presentation.BaseContract

sealed interface MenuContract {

    sealed interface MenuEvent : BaseContract.BaseEvent {
        // Действия с баннерами
        data class BannerClick(val banner: Banner) : MenuEvent

        // Сброс состояния
        data object ResetSelectedMenuItemIndex : MenuEvent

        // Обновление данных
        data object ForceRefresh : MenuEvent
        data object GetActiveOrders : MenuEvent
    }

    sealed interface MenuEffect : BaseContract.BaseEffect

    data class MenuState(
        val isLoading: Boolean = false,
        val menuItems: List<MenuItem> = emptyList(),
        val favoriteIds: Set<String> = emptySet(),
        val error: UiError? = null,
        val banners: List<Banner> = emptyList(),
        /** Баннер про закрытие / выходной; `null` — не показывать. */
        val orderClosingBanner: OrderClosingBannerUi? = null,
        val announcements: List<String> = emptyList(),
        val bannersAreLoading: Boolean = false,
        val selectedMenuItemIndex: Int = DEFAULT_UNSELECTED_INDEX,
        val activeOrders: List<SavedOrder> = emptyList(),
    ) : BaseContract.BaseState
}
