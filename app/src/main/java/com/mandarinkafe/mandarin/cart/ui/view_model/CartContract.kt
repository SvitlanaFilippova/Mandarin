package com.mandarinkafe.mandarin.cart.ui.view_model

import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.util.ui.BottomSheetEffect

sealed interface CartContract {
    sealed interface Event {
        data object GetCart : Event
        data class AddToCart(val meal: Meal) : Event
        data class RemoveFromCart(val meal: Meal) : Event
        data class ReplaceMealInCart(val newMeal: Meal, val oldMeal: Meal) : Event
        data class CancelRemove(val meal: Meal) : Event
        data object ClearCart : Event
        data object CancelClearingCart : Event
        data class EditMeal(val meal: Meal) : Event
        data class OpenMealDetails(val meal: Meal) : Event
    }

    sealed interface Effect {
        data class OpenMealDetailsBS(val meal: Meal, val shouldOpenCustomization: Boolean = false) :
            Effect, BottomSheetEffect
    }

    data class State(
        val isLoading: Boolean = true,
        val isPendingDeletion: Boolean = false,
        val cartItems: List<CartItem> = emptyList(),
        val pendingDeletionMeals: List<Meal> = emptyList(),
        val mealDeletionProgress: Map<Meal, Float> = emptyMap(),
        val cartClearingProgress: Float? = null
    ) {
        val totalCartPrice: Int
            get() = cartItems
                .filter { it.meal !in pendingDeletionMeals }
                .sumOf { it.meal.price * it.quantity }
    }
}