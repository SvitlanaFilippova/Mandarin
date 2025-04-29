package com.mandarinkafe.mandarin.cart.ui.view_model

import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.cart.totalPrice
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.util.ui.BottomSheetEffect

sealed interface CartContract {
    sealed interface Event {
        data object GetCart : Event
        data class AddToCart(val item: CartItem) : Event
        data class RemoveFromCartWithDelay(val item: CartItem) : Event
        data class RemoveFromCartByMeal(val meal: Meal) : Event
        data class ReplaceMealInCart(val newItem: CartItem, val oldItem: CartItem) : Event
        data class CancelRemove(val item: CartItem) : Event
        data object ClearCart : Event
        data object CancelClearingCart : Event
        data class OpenMealDetails(val item: CartItem) : Event
    }

    sealed interface Effect {
        data class OpenMealDetailsBS(
            val item: CartItem
        ) :
            Effect, BottomSheetEffect
    }

    data class State(
        val isLoading: Boolean = true,
        val isPendingDeletion: Boolean = false,
        val cartItems: Map<CartItem, Int> = emptyMap(),
        val recommendsList: List<CartItem> = emptyList(),
        val pendingDeletionMeals: List<CartItem> = emptyList(),
        val mealDeletionProgress: Map<CartItem, Float> = emptyMap(),
        val cartClearingProgress: Float? = null
    ) {
        val totalCartPrice: Int
            get() = cartItems
                .filter { (item, _) -> item !in pendingDeletionMeals }
                .entries
                .sumOf { (item, quantity) ->
                    item.totalPrice() * quantity
                }
    }
}