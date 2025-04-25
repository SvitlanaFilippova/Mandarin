package com.mandarinkafe.mandarin.cart.ui.view_model

import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.util.ui.BottomSheetEffect

sealed interface CartContract {
    sealed interface Event {
        data object GetCart : Event
        data class AddToCart(val meal: Meal) : Event
        data class RemoveFromCart(val meal: Meal) : Event
        data class EditMeal(val meal: Meal) : Event
        data class CancelRemove(val meal: Meal) : Event
        data object ClearCart : Event
    }

    sealed interface Effect {
        data class OpenEditMealBS(val meal: Meal) : Effect, BottomSheetEffect
    }

    data class State(
        val isLoading: Boolean = true,
        val cartItems: List<CartItem> = emptyList(),
        val pendingDeletionItems: List<Meal> = emptyList(),
        val totalCartPrice: Int = 0
    )
}