package com.mandarinkafe.mandarin.features.cart.ui.view_model

import com.mandarinkafe.mandarin.core.BaseEffect
import com.mandarinkafe.mandarin.core.BaseEvent
import com.mandarinkafe.mandarin.core.BaseState
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.extensions.totalPrice
import com.mandarinkafe.mandarin.core.ui.models.UiError
import com.mandarinkafe.mandarin.util.ui.BottomSheetEffect

sealed interface CartContract {
    sealed interface CartEvent : BaseEvent {
        data class AddToCart(val item: CustomizedMeal) : CartEvent
        data class RemoveFromCartWithDelay(val item: CustomizedMeal) : CartEvent
        data class RemoveFromCartByItem(val item: CustomizedMeal) : CartEvent
        data class RemoveFromCartByMeal(val meal: Meal) : CartEvent
        data class ReplaceMealInCart(val newItem: CustomizedMeal, val oldItem: CustomizedMeal) :
            CartEvent
        data class ToggleFavorite(val item: CustomizedMeal) : CartEvent
        data class CancelRemove(val item: CustomizedMeal) : CartEvent
        data object ClearCart : CartEvent
        data object ConfirmClearCart : CartEvent
        data class OpenMealDetails(val item: CustomizedMeal) : CartEvent
    }

    sealed interface CartEffect : BaseEffect {
        data class OpenMealDetailsBS(
            val item: CustomizedMeal
        ) :
            CartEffect, BottomSheetEffect

        data object ShowClearCartConfirmationDialog : CartEffect
    }

    data class CartState(
        val isLoading: Boolean = true,
        val error: UiError? = null,
        val cartItems: Map<CustomizedMeal, Int> = emptyMap(),
        val recommends: List<CustomizedMeal> = emptyList(),
        val pendingDeletionMeals: List<CustomizedMeal> = emptyList(),
        val mealDeletionProgress: Map<CustomizedMeal, Float> = emptyMap(),
    ) : BaseState {
        val totalCartPrice: Int
            get() = cartItems
                .filter { (item, _) -> item !in pendingDeletionMeals }
                .entries
                .sumOf { (item, quantity) ->
                    item.totalPrice() * quantity
                }
        val cartItemsCount: Int
            get() = cartItems
                .filter { (item, _) -> item !in pendingDeletionMeals }
                .values
                .sum()
    }
}