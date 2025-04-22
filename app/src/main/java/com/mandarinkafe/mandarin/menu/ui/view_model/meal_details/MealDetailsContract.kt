package com.mandarinkafe.mandarin.menu.ui.view_model.meal_details

import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MealAdditional
import com.mandarinkafe.mandarin.menu.domain.models.MealCategory
import com.mandarinkafe.mandarin.util.Constants.DEFAULT_SELECTED_FIRST_INDEX

sealed interface MealDetailsContract {
    sealed interface Event {
        data object GetAddons : Event
        data class ChangeAdds(val add: MealAdditional, val isAdded: Boolean) : Event
        data class SetMeal(val meal: Meal) : Event
        data object ToggleFavorite : Event
        data object AddToCart : Event
        data class ChooseCategory(val newIndex: Int) : Event
    }

    data class State(
        val isLoading: Boolean = false,
        val meal: Meal? = null,
        val pizzaAds: List<MealCategory> = emptyList(),
        val sumPrice: Int = 0,
        val errorMessage: String? = null,
        val selectedTabIndex: Int = DEFAULT_SELECTED_FIRST_INDEX,
    )
}