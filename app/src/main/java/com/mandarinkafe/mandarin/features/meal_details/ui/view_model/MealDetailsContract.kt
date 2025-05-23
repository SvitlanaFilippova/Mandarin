package com.mandarinkafe.mandarin.features.meal_details.ui.view_model

import com.mandarinkafe.mandarin.core.BaseEffect
import com.mandarinkafe.mandarin.core.BaseEvent
import com.mandarinkafe.mandarin.core.BaseState
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.ModifierItem
import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.util.Constants.DEFAULT_SELECTED_FIRST_INDEX

sealed interface MealDetailsContract {
    sealed interface MealDetailsEvent : BaseEvent {
        data class ChangeAdds(val add: MealAdditional, val isChecked: Boolean) : MealDetailsEvent
        data class ChooseSingleModifier(val modifierGroup: ModifierGroup) : MealDetailsEvent
        data class ChooseMultiModifiers(
            val modifierGroup: ModifierGroup, val modifierItem: ModifierItem,
            val isChecked: Boolean
        ) : MealDetailsEvent

        data class SetItem(val item: CustomizedMeal) : MealDetailsEvent
        data object ToggleFavorite : MealDetailsEvent
        data class ChooseCategory(val newIndex: Int) : MealDetailsEvent
    }

    sealed interface MealDetailsEffect : BaseEffect

    data class MealDetailsState(
        val isLoading: Boolean = false,
        val customizedMeal: CustomizedMeal? = null,
        val pizzaAds: List<MealAdditionalCategory> = emptyList(),
        val errorMessage: String? = null,
        val selectedTabIndex: Int = DEFAULT_SELECTED_FIRST_INDEX,
    ) : BaseState
}