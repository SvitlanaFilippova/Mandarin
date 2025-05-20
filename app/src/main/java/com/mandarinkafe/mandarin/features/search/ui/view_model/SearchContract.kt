package com.mandarinkafe.mandarin.features.search.ui.view_model

import com.mandarinkafe.mandarin.core.BaseEffect
import com.mandarinkafe.mandarin.core.BaseEvent
import com.mandarinkafe.mandarin.core.BaseState
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.search.ui.model.LabelUiModel
import com.mandarinkafe.mandarin.util.ui.BottomSheetEffect

interface SearchContract {

    sealed interface SearchEvent : BaseEvent {
        data class OnLabelClick(val labelName: String, val isChecked: Boolean) :
            SearchEvent

        data class SearchMealsByText(val searchText: String) : SearchEvent
        data object ClearSearchInput : SearchEvent
        data class ToggleFavorite(val meal: Meal) : SearchEvent
        data class UpdateMealFavorite(val id: String, val isFavorite: Boolean) : SearchEvent
        data class OnMealDetailsClick(val meal: Meal) : SearchEvent
    }

    sealed interface SearchEffect : BaseEffect {
        data class OpenMealDetailsBS(val meal: Meal) :
            SearchEffect, BottomSheetEffect
    }

    data class SearchState(
        val fullMealList: List<Meal> = emptyList(),
        val filteredMealList: List<Meal> = emptyList(),
        val allLabels: List<LabelUiModel> = emptyList(),
        val checkedLabels: List<String> = emptyList(),
        val latestSearchText: String = "",
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
    ) : BaseState
}

