package com.mandarinkafe.mandarin.features.search.ui.view_model

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.ui.models.UiError
import com.mandarinkafe.mandarin.features.search.ui.model.LabelUiModel
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState

interface SearchContract {

    sealed interface SearchEvent : BaseEvent {
        data class OnLabelClick(val labelName: String, val isChecked: Boolean) :
            SearchEvent

        data class SearchMealsByText(val searchText: String) : SearchEvent
        data object ClearSearchInput : SearchEvent
    }

    sealed interface SearchEffect : BaseEffect

    data class SearchState(
        val fullMealList: List<Meal> = emptyList(),
        val filteredMealList: List<Meal> = emptyList(),
        val favoritesIds: Set<String> = emptySet(),
        val allLabels: List<LabelUiModel> = emptyList(),
        val checkedLabels: List<String> = emptyList(),
        val latestSearchText: String = "",
        val isLoading: Boolean = false,
        val error: UiError? = null,
    ) : BaseState
}

