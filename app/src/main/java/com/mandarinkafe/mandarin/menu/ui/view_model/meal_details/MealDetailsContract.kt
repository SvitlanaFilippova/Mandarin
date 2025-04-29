package com.mandarinkafe.mandarin.menu.ui.view_model.meal_details

import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.util.Constants.DEFAULT_SELECTED_FIRST_INDEX

sealed interface MealDetailsContract {
    sealed interface Event {
        data class ChangeAdds(val add: MealAdditional, val isChecked: Boolean) : Event
        data class ChooseModifiers(val modifierGroup: ModifierGroup) : Event
        data class SetItem(val item: CartItem) : Event
        data object ToggleFavorite : Event
        data class ChooseCategory(val newIndex: Int) : Event
    }

    data class State(
        val isLoading: Boolean = false,
        val meal: Meal? = null,
        val customizedMeal: CartItem? = null,
        val pizzaAds: List<MealAdditionalCategory> = emptyList(),
        val errorMessage: String? = null,
        val selectedTabIndex: Int = DEFAULT_SELECTED_FIRST_INDEX,
    )
}