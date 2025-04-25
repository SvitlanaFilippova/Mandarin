package com.mandarinkafe.mandarin.cart.ui.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.cart.domain.usecase.CartInteractor
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract.Effect
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract.Effect.OpenEditMealBS
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract.Event
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.util.UndoActionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _effect =
        MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    private val undoManager = UndoActionManager<Meal>(viewModelScope)

    init {
        onEvent(Event.GetCart)
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.GetCart -> updateCartState()
            is Event.AddToCart -> addItem(event.meal)
            is Event.RemoveFromCart -> removeItem(event.meal)
            is Event.EditMeal -> sendEffect(OpenEditMealBS(event.meal))
            is Event.CancelRemove -> cancelRemove(event.meal)
            Event.ClearCart -> clear()

        }
    }

    private fun addItem(meal: Meal) {
        cartInteractor.addToCart(meal)

        _state.update { currentState ->
            val updatedList = currentState.cartItems.toMutableList()
            val index = updatedList.indexOfFirst { it.meal.id == meal.id }

            if (index != -1) {
                val item = updatedList[index]
                updatedList[index] = item.copy(quantity = item.quantity + 1)
            } else {
                updatedList.add(CartItem(meal = meal, quantity = 1))
            }

            currentState.copy(
                cartItems = updatedList,
                totalCartPrice = calculateTotalPrice(updatedList)
            )
        }
        Log.d("DEBUG Cart", "CartViewModel - addItem, meal: ${meal.name} + ${meal.adds}")
    }

    fun removeWithUndo(meal: Meal) {
        undoManager.schedule(meal) {
            cartInteractor.removeFromCart(meal)
            updateCartState()
        }
        // можно отобразить "удалено, отменить?"
    }

    private fun cancelRemove(meal: Meal) {
        undoManager.cancel(meal)
        //  скрыть уведомление
    }

    private fun removeItem(meal: Meal) {
        cartInteractor.removeFromCart(meal)

        _state.update { currentState ->
            val updatedList = currentState.cartItems.toMutableList()
            val index = updatedList.indexOfFirst { it.meal.id == meal.id }

            if (index != -1) {
                val item = updatedList[index]
                if (item.quantity > 1) {
                    updatedList[index] = item.copy(quantity = item.quantity - 1)
                } else {
                    updatedList.removeAt(index)
                }
            }
            currentState.copy(
                cartItems = updatedList,
                totalCartPrice = calculateTotalPrice(updatedList)
            )
        }
        Log.d("DEBUG Cart", "CartViewModel - removeItem, meal: $meal")
    }

    private fun updateCartState() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val cartItems = cartInteractor.getCart()
            _state.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    cartItems = cartItems,
                    totalCartPrice = calculateTotalPrice(cartItems)
                )
            }
            Log.d("DEBUG Cart", "CartViewModel - updateCartState, cartItems: $cartItems")
        }
    }

    private fun clear() {
        cartInteractor.clearCart()
        _state.update {
            it.copy(cartItems = emptyList(), totalCartPrice = 0)
        }
        Log.d("DEBUG Cart", "CartViewModel - clear")
    }

    private fun calculateTotalPrice(items: List<CartItem>): Int {
        return items.sumOf { item ->
            val basePrice = item.meal.price
            val addsPrice = item.meal.adds.sumOf { it.price }
            (basePrice + addsPrice) * item.quantity
        }
    }

    private fun sendEffect(effect: Effect) {
        viewModelScope.launch { _effect.emit(effect) }
    }

}