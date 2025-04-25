package com.mandarinkafe.mandarin.cart.ui.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.cart.domain.usecase.CartInteractor
import com.mandarinkafe.mandarin.cart.domain.util.indexOfMeal
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract.Effect
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract.Effect.OpenEditMealBS
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract.Event
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.totalPrice
import com.mandarinkafe.mandarin.util.Constants.DELETE_FROM_CART_DEBOUNCE_DELAY
import com.mandarinkafe.mandarin.util.Constants.INTERVAL_FOR_UPD_PROGRESSBAR
import com.mandarinkafe.mandarin.util.debounce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    init {
        onEvent(Event.GetCart)
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.GetCart -> updateCartState()
            is Event.AddToCart -> addItem(event.meal)
            is Event.RemoveFromCart -> onReduceItem(event.meal)
            is Event.EditMeal -> sendEffect(OpenEditMealBS(event.meal))
            is Event.CancelRemove -> cancelRemove(event.meal)
            Event.ClearCart -> clear()

        }
    }

    private fun addItem(meal: Meal) {
        cartInteractor.addToCart(meal)

        _state.update { currentState ->
            val updatedList = currentState.cartItems.toMutableList()
            val index = updatedList.indexOfMeal(meal)

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

    private val removeDebounce = debounce<Meal>(
        DELETE_FROM_CART_DEBOUNCE_DELAY,
        viewModelScope,
        useLastParam = false
    ) { meal ->
        removeItem(meal)
    }

    private fun cancelRemove(meal: Meal) {
        removeDebounce.cancel()
        Log.d("Debug UNDO Delete", "CartViewModel, cancelRemove for $meal")

        _state.update { currentState ->
            val currentCartList = currentState.cartItems
            val updatedPendingDeletionItems =
                currentState.pendingDeletionItems.toMutableList() - meal
            val updatedDeletionProgress = currentState.deletionProgress.toMutableMap()

            updatedDeletionProgress.entries.removeIf { it.key == meal } // <--- и тут

            currentState.copy(
                pendingDeletionItems = updatedPendingDeletionItems,
                totalCartPrice = calculateTotalPrice(currentCartList),
                deletionProgress = updatedDeletionProgress,
            )
        }
    }

    private fun onReduceItem(meal: Meal) {

        // нужно проработать ситуацию, когда в корзине пицца с добавками, а "-" вызывается с общей карточки а не для конкретной пиццы
        // В этом случае нужно делать поиск по ID и удалять последнюю добавленную с тем же ID, а не полный дубль meal (его просто не будет)

        _state.update { currentState ->
            val currentCartList = currentState.cartItems
            var updatedCartList = currentCartList.toMutableList()
            val updatedPendingDeletionItems = currentState.pendingDeletionItems.toMutableList()

            val index = currentCartList.indexOfMeal(meal)

            if (index != -1) {
                val item = currentCartList[index]
                if (item.quantity > 1) {
                    updatedCartList[index] = item.copy(quantity = item.quantity - 1)
                    cartInteractor.removeFromCart(meal)

                } else {
                    updatedCartList = currentCartList.toMutableList()
                    updatedPendingDeletionItems.add(meal)
                    Log.d(
                        "Debug UNDO Delete", "CartViewModel, onReduceItem, " +
                                " meal ${meal.name}, В списке на удаление: ${
                                    updatedPendingDeletionItems.contains(
                                        meal
                                    )
                                }"
                    )
                    removeDebounce.invoke(meal)
                    startProgressTimer(meal)

                }
            }
            currentState.copy(
                cartItems = updatedCartList,
                pendingDeletionItems = updatedPendingDeletionItems,
                totalCartPrice = calculateTotalPrice(updatedCartList, updatedPendingDeletionItems)
            )
        }
        Log.d("DEBUG Cart", "CartViewModel - removeItem, meal: $meal")
    }

    private fun removeItem(meal: Meal) {
        _state.update { currentState ->
            val updatedCartList = currentState.cartItems.toMutableList()
            val updatedPendingDeletionItems = currentState.pendingDeletionItems.toMutableList()
            val updatedDeletionProgress = currentState.deletionProgress.toMutableMap()

            val index = updatedCartList.indexOfMeal(meal)

            if (index != -1) {
                updatedCartList.removeAt(index)
                updatedPendingDeletionItems.remove(meal)
                updatedDeletionProgress.entries.removeIf { it.key == meal }
            }
            cartInteractor.removeFromCart(meal)
            currentState.copy(
                cartItems = updatedCartList,
                pendingDeletionItems = updatedPendingDeletionItems,
                totalCartPrice = calculateTotalPrice(updatedCartList),
                deletionProgress = updatedDeletionProgress,
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

    private fun startProgressTimer(meal: Meal) {
        val duration = DELETE_FROM_CART_DEBOUNCE_DELAY
        val interval = INTERVAL_FOR_UPD_PROGRESSBAR
        val steps = (duration / interval).toInt()

        viewModelScope.launch {
            repeat(steps) { step ->
                delay(interval)
                val progress = step / steps.toFloat()
                _state.update { state ->
                    state.copy(
                        deletionProgress = state.deletionProgress + (meal to progress)
                    )
                }
            }
        }
    }

    private fun clear() {
        cartInteractor.clearCart()
        _state.update {
            it.copy(cartItems = emptyList(), totalCartPrice = 0)
        }
        Log.d("DEBUG Cart", "CartViewModel - clear")
    }

    private fun calculateTotalPrice(
        itemsInCart: List<CartItem> = emptyList(),
        pendingDeletionMeals: List<Meal> = emptyList()
    ): Int {
        val itemsPrice = itemsInCart.sumOf { it.meal.totalPrice() * it.quantity }
        val mealsPrice = pendingDeletionMeals.sumOf { it.totalPrice() }
        return itemsPrice - mealsPrice
    }

    private fun sendEffect(effect: Effect) {
        viewModelScope.launch { _effect.emit(effect) }
    }

}