package com.mandarinkafe.mandarin.cart.ui.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.cart.domain.usecase.CartInteractor
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract.Event
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartInteractor: CartInteractor
) : ViewModel() {
    private val _state =
        MutableStateFlow(CartContract.State())
    val state: StateFlow<CartContract.State> = _state.asStateFlow()

    init {
        onEvent(Event.GetCart)
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.GetCart -> updateCartState()
            is Event.AddToCart -> addItem(event.meal)
            is Event.RemoveFromCart -> removeItem(event.meal)
            Event.ClearCart -> clear()
        }
        Log.d("DEBUG Cart", "CartViewModel - onEvent, Event: $event")
    }

    private fun addItem(meal: Meal) {
        cartInteractor.addToCart(meal)
        updateCartState()
        Log.d("DEBUG Cart", "CartViewModel - addItem, meal: $meal")
    }

    private fun removeItem(meal: Meal) {
        cartInteractor.removeFromCart(meal)
        updateCartState()
    }

    private fun updateCartState() {
        viewModelScope.launch {
            val cartItems = cartInteractor.getCart()
            val totalCartPrice = cartItems.sumOf {
                it.meal.price + it.meal.adds.sumOf { it.price }
            }

        _state.update { currentState ->
            currentState.copy(
                cartItems = cartItems,
                totalCartPrice = totalCartPrice
            )
        }
            Log.d("DEBUG Cart", "CartViewModel - updateCartState, cartItems: $cartItems")
        }
    }

    private fun clear() {
        cartInteractor.clearCart()
        _state.update {
            it.copy(cartItems = emptyList())
        }
        Log.d("DEBUG Cart", "CartViewModel - clear")
    }
}