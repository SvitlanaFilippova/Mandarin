package com.mandarinkafe.mandarin.cart.ui

import com.mandarinkafe.mandarin.menu.domain.models.Meal

sealed interface CartContract {
    sealed interface Event {
        data class AddToCart(val meal: Meal) : Event
        data class RemoveFromCart(val meal: Meal) : Event
        data object ClearCart : Event
    }

    data class State(
        val cartItems: List<Meal> = emptyList(),
    )
}