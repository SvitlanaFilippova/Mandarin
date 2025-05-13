package com.mandarinkafe.mandarin.features.menu.ui.view_model

import com.mandarinkafe.mandarin.core.BaseEffect
import com.mandarinkafe.mandarin.core.BaseEvent
import com.mandarinkafe.mandarin.core.BaseState
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.menu.domain.models.MenuItem
import com.mandarinkafe.mandarin.util.Constants.DEFAULT_UNSELECTED_INDEX
import com.mandarinkafe.mandarin.util.ui.BottomSheetEffect

sealed interface MenuContract {

    sealed interface MenuEvent : BaseEvent {
        // Загрузка меню
        data object LoadMenu : MenuEvent
        data object ForceRefreshMenu : MenuEvent

        // Избранные
        data class ToggleFavorite(val meal: Meal) : MenuEvent
        data class UpdateMealFavorite(val id: String, val isFavorite: Boolean) : MenuEvent

        // Скролл по меню
        data object ScrollToTop : MenuEvent
        data class ScrollToCategory(val newIndex: Int) : MenuEvent
        data class ScrollToSubCategory(val newIndex: Int) : MenuEvent
        data object ResetSelectedMenuItemIndex : MenuEvent
        data class BannerClick(val targetName: String) : MenuEvent

        // Детали блюда
        data class OnMealDetailsClick(val meal: Meal) : MenuEvent

        // Позвонить
        data object OnPhoneClick : MenuEvent

        // Поиск и фильтрация
        data object SearchOnOpenSearchClick : MenuEvent
        data object OnOpenFavoritesClick : MenuEvent
        data object OnLabelsClick : MenuEvent
    }

    sealed interface MenuEffect : BaseEffect {
        data class ShowSnackbar(val message: String) : MenuEffect
        data class OpenSearch(val focusSearch: Boolean) : MenuEffect
        data object OpenFavorites : MenuEffect
        data object CallPhone : MenuEffect
        data class OpenMealDetailsBS(val meal: Meal) :
            MenuEffect, BottomSheetEffect
    }

    data class MenuState(
        val isLoading: Boolean = false,
        val menuItems: List<MenuItem> = emptyList(),
        val errorMessage: String? = null,
        val selectedTabIndex: Int = DEFAULT_UNSELECTED_INDEX,
        val selectedSubTabIndex: Int = DEFAULT_UNSELECTED_INDEX,
        val selectedMenuItemIndex: Int = DEFAULT_UNSELECTED_INDEX,
    ) : BaseState
}