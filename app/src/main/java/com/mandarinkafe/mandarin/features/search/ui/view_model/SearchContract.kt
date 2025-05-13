package com.mandarinkafe.mandarin.features.search.ui.view_model

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.menu.domain.models.MenuItem
import com.mandarinkafe.mandarin.features.search.ui.model.LabelUiModel
import com.mandarinkafe.mandarin.util.ui.BottomSheetEffect

interface SearchContract {

    sealed interface Event {
        data class OnLabelClick(val labelName: String, val isChecked: Boolean) :
            Event

        data class SearchMealsByText(val searchText: String) : Event
        data object ClearSearchInput : Event
        data class ToggleFavorite(val meal: Meal) : Event
        data class UpdateMealFavorite(val id: String, val isFavorite: Boolean) : Event
        data class OnMealDetailsClick(val meal: Meal) : Event
    }

    sealed interface Effect {
        data class OpenMealDetailsBS(val meal: Meal) :
            Effect, BottomSheetEffect
    }

    data class State(
        val menuItems: List<MenuItem> = emptyList(),
        val filteredMenuItems: List<MenuItem> = emptyList(),
        val allLabels: List<LabelUiModel> = emptyList(),
        val checkedLabels: List<String> = emptyList(),
        val latestSearchText: String = "",
        val isLoading: Boolean = false,
        val errorMessage: String? = null,

        )
}

