package com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.ModifierItem
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState
import com.mandarinkafe.mandarin.util.Constants.DEFAULT_SELECTED_FIRST_INDEX

sealed interface MealDetailsContract {
    sealed interface MealDetailsEvent : BaseEvent {
        // Установка блюда при инициализации
        data class SetInitItem(val item: CustomizedMeal) : MealDetailsEvent

        // Управление добавками
        data class ChangeAdds(val add: MealAdditional, val isChecked: Boolean) : MealDetailsEvent

        // Выбор модификаторов
        data class ChooseSingleModifier(val modifierGroup: ModifierGroup) : MealDetailsEvent
        data class ChooseMultiModifiers(
            val modifierGroup: ModifierGroup,
            val modifierItem: ModifierItem,
            val isChecked: Boolean
        ) : MealDetailsEvent

        // Навигация по категориям
        data class ChooseCategory(val newIndex: Int) : MealDetailsEvent

        // Обработка действий с корзиной
        data object OnToCartClickBeforeMandatoryChoice : MealDetailsEvent
    }

    sealed interface MealDetailsEffect : BaseEffect {
        data object ShowRequiredModifiersDialog : MealDetailsEffect
        data class ShowMaxModifiersQuantity(val groupName: String, val max: Int) : MealDetailsEffect
    }

    data class MealDetailsState(
        val isLoading: Boolean = false,
        val error: UiError? = null,
        val customizedMeal: CustomizedMeal? = null,
        val addons: List<MealAdditionalCategory> = emptyList(),
        val errorMessage: String? = null,
        val selectedTabIndex: Int = DEFAULT_SELECTED_FIRST_INDEX,
    ) : BaseState
}