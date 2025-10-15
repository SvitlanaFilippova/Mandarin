package com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.ModifierItem
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.util.presentation.BaseEffect
import com.mandarinkafe.mandarin.util.presentation.BaseEvent
import com.mandarinkafe.mandarin.util.presentation.BaseState
import com.mandarinkafe.mandarin.util.Constants.DEFAULT_SELECTED_FIRST_INDEX
import com.mandarinkafe.mandarin.util.presentation.UiText

sealed interface MealDetailsContract {
    sealed interface MealDetailsEvent : BaseEvent {
        // Установка блюда при инициализации
        data class SetInitData(
            val item: CartItem?,
            val mealId: String?,
            val isEditMode: Boolean,
        ) : MealDetailsEvent

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

        // Комментарий
        data class SetComment(val text: String) : MealDetailsEvent

        // Обработка действий с корзиной
        data object OnToCartClickBeforeMandatoryChoice : MealDetailsEvent
        data class TryAddMeal(val item: CartItem?) : MealDetailsEvent
        data class EditMealInCart(val newItem: CartItem, val oldItem: CartItem? = null) :
            MealDetailsEvent
    }

    sealed interface MealDetailsEffect : BaseEffect {
        data object ShowRequiredModifiersDialog : MealDetailsEffect
        data class ShowMaxModifiersQuantity(val groupName: String, val max: Int) : MealDetailsEffect
        data class AskReplaceOrAdd(
            val message: UiText,
            val onAddNew: () -> Unit,
            val onReplace: () -> Unit
        ) : MealDetailsEffect

        data class CloseAndShowMessage(val message: UiText? = null) : MealDetailsEffect
    }

    data class MealDetailsState(
        val isLoading: Boolean = false,
        val error: UiError? = null,
        val isEditMode: Boolean = false,
        val initItem: CartItem? = null,
        val customizedMeal: CustomizedMeal? = null,
        val comment: String = "",
        val addons: List<MealAdditionalCategory> = emptyList(),
        val errorMessage: String? = null,
        val selectedTabIndex: Int = DEFAULT_SELECTED_FIRST_INDEX,
    ) : BaseState {

        val actualCartItem: CartItem?
            get() = customizedMeal?.let { meal ->
                if (isEditMode) {
                    initItem?.copy(customizedMeal = meal, comment = comment)
                } else {
                    CartItem(customizedMeal = meal, comment = comment)
                }
            }
    }
}