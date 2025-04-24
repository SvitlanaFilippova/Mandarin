package com.mandarinkafe.mandarin.cart.ui

import androidx.lifecycle.ViewModel
import com.mandarinkafe.mandarin.cart.ui.CartContract.Event
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
) : ViewModel() {
    private val _state =
        MutableStateFlow(CartContract.State())
    val state: StateFlow<CartContract.State> = _state.asStateFlow()

    fun onEvent(event: Event) {
        when (event) {
            is Event.AddToCart -> addItem(event.meal)
            is Event.RemoveFromCart -> removeItem(event.meal)
            Event.ClearCart -> clear()
        }
    }

    private fun addItem(meal: Meal) {
        _state.update { currentState ->
            val currentItems = currentState.cartItems
            currentState.copy(cartItems = currentItems + meal)
        }
    }

    private fun removeItem(meal: Meal) {
        _state.update { currentState ->
            val currentItems = currentState.cartItems
            currentState.copy(cartItems = currentItems - meal)
        }
    }

    private fun clear() {
        _state.update {
            it.copy(cartItems = emptyList())
        }
    }
}