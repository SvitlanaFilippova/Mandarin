package com.mandarinkafe.mandarin.features.cart.ui.view_model

import com.mandarinkafe.mandarin.core.BaseEffect
import com.mandarinkafe.mandarin.core.BaseEvent
import com.mandarinkafe.mandarin.core.BaseState
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.features.cart.totalPrice
import com.mandarinkafe.mandarin.util.ui.BottomSheetEffect

sealed interface CartContract {
    sealed interface CartEvent : BaseEvent {
        data object GetCart : CartEvent
        data class AddToCart(val item: CartItem) : CartEvent
        data class RemoveFromCartWithDelay(val item: CartItem) : CartEvent
        data class RemoveFromCartByMeal(val meal: Meal) : CartEvent
        data class ReplaceMealInCart(val newItem: CartItem, val oldItem: CartItem) : CartEvent
        data class CancelRemove(val item: CartItem) : CartEvent
        data object ClearCart : CartEvent
        data object ConfirmClearCart : CartEvent
        data class OpenMealDetails(val item: CartItem) : CartEvent
    }

    sealed interface CartEffect : BaseEffect {
        data class OpenMealDetailsBS(
            val item: CartItem
        ) :
            CartEffect, BottomSheetEffect
        data object ShowClearCartConfirmationDialog : CartEffect
    }

    data class CartState(
        val isLoading: Boolean = true,
        val cartItems: Map<CartItem, Int> = emptyMap(),
        val recommendsList: List<CartItem> = emptyList(),
        val pendingDeletionMeals: List<CartItem> = emptyList(),
        val mealDeletionProgress: Map<CartItem, Float> = emptyMap(),
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