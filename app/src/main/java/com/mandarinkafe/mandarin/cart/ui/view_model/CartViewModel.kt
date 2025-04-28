package com.mandarinkafe.mandarin.cart.ui.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.cart.CartMapper.toCartItem
import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.cart.domain.usecase.CartInteractor
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract.Effect
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract.Effect.OpenMealDetailsBS
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract.Event
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.util.Constants.CLEAR_CART_DEBOUNCE_DELAY
import com.mandarinkafe.mandarin.util.Constants.DELETE_FROM_CART_DEBOUNCE_DELAY
import com.mandarinkafe.mandarin.util.Constants.INTERVAL_FOR_UPD_PROGRESSBAR
import com.mandarinkafe.mandarin.util.Constants.UPD_RECOMMEND_AFTER_CART_CHANGE_DEBOUNCE
import com.mandarinkafe.mandarin.util.debounce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
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
    private val itemTimers = mutableMapOf<CartItem, Job>()
    private var clearCartTimerJob: Job? = null

    init {
        onEvent(Event.GetCart)
        observeCartChanges()
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.GetCart -> updateCartState()
            is Event.AddToCart -> addItem(item = event.item)
            is Event.ReplaceMealInCart -> replaceMealInCart(
                newItem = event.newItem,
                oldItem = event.oldItem
            )

            is Event.RemoveFromCartWithDelay -> onReduceItem(item = event.item)
            is Event.RemoveFromCartByMeal -> removeFromCartByMeal(meal = event.meal)
            is Event.CancelRemove -> cancelRemove(item = event.item)
            is Event.ClearCart -> clearCartWithDebounce()
            is Event.CancelClearingCart -> cancelClearingCart()
            is Event.OpenMealDetails -> sendEffect(
                OpenMealDetailsBS(
                    item = event.item,
                    shouldOpenCustomization = false
                )
            )

            is Event.EditMeal -> sendEffect(
                OpenMealDetailsBS(
                    item = event.item,
                    shouldOpenCustomization = true
                )
            )
        }
    }

    private fun replaceMealInCart(newItem: CartItem, oldItem: CartItem) {
        cartInteractor.removeFromCart(oldItem)
        cartInteractor.addToCart(newItem)

        _state.update { currentState ->
            val updatedMap = currentState.cartItems.toMutableMap()
            val oldQuantity = updatedMap.remove(oldItem) ?: 1
            // Если старая позиция найдена, переносим её количество в новую
            updatedMap[newItem] = oldQuantity

            currentState.copy(
                cartItems = updatedMap
            )
        }
    }

    private fun sendEffect(effect: Effect) {
        viewModelScope.launch { _effect.emit(effect) }
    }

    private val removeDebounce = debounce<CartItem>(
        DELETE_FROM_CART_DEBOUNCE_DELAY,
        viewModelScope,
        useLastParam = true
    ) { item ->
        removeItem(item)
    }

    private fun clearCartWithDebounce() {
        _state.update { it.copy(isPendingDeletion = true) }
        startProgressTimer(duration = CLEAR_CART_DEBOUNCE_DELAY)
        clearCartDebounce.invoke(Unit)
    }

    private fun addItem(item: CartItem) {
        cartInteractor.addToCart(item)
        _state.update { currentState ->
            val cartItems = currentState.cartItems
            val currentQuantity = cartItems[item] ?: 0
            val newCartItems = cartItems.toMutableMap().apply {
                put(item, currentQuantity + 1)
            }
            currentState.copy(
                cartItems = newCartItems
            )
        }
    }

    // Обработка кнопки "-". Если в корзине была 1 шт - запуск отложенного удаления
    private fun onReduceItem(item: CartItem) {
        _state.update { currentState ->
            val pendingDeletionItems = currentState.pendingDeletionMeals.toMutableList()
            val cartItems = currentState.cartItems
            val currentQuantity = cartItems[item] ?: 0
            val updatedCartList = cartItems.toMutableMap().apply {
                // если в корзине  больше одной штуки item
                if (currentQuantity > 1) {
                    put(item, currentQuantity - 1)
                    cartInteractor.removeFromCart(item)
                } else {
                    removeDebounce.invoke(item)
                    startProgressTimer(item = item, duration = DELETE_FROM_CART_DEBOUNCE_DELAY)
                    pendingDeletionItems.add(item)
                }
            }

            currentState.copy(
                cartItems = updatedCartList,
                pendingDeletionMeals = pendingDeletionItems,
            )
        }
    }

    // отмена удаления
    private fun cancelRemove(item: CartItem) {
        removeDebounce.cancel()
        cancelMealDeletionTimer(item)

        _state.update { currentState ->
            currentState.cartItems
            val updatedPendingDeletionItems =
                currentState.pendingDeletionMeals.toMutableList() - item
            val updatedDeletionProgress = currentState.mealDeletionProgress.toMutableMap()

            updatedDeletionProgress.entries.removeIf { it.key == item }

            currentState.copy(
                pendingDeletionMeals = updatedPendingDeletionItems,
                mealDeletionProgress = updatedDeletionProgress,
            )
        }
    }

    // Окончательное удаление из корзины
    private fun removeItem(item: CartItem) {
        _state.update { currentState ->
            val pendingDeletionItems = currentState.pendingDeletionMeals.toMutableList()
            val deletionProgress = currentState.mealDeletionProgress.toMutableMap()

            val updatedCartList = currentState.cartItems.toMutableMap().apply {
                remove(item)
            }

            pendingDeletionItems.remove(item)
            deletionProgress.entries.removeIf { it.key == item }
            cartInteractor.removeFromCart(item)

            currentState.copy(
                cartItems = updatedCartList,
                pendingDeletionMeals = pendingDeletionItems,
                mealDeletionProgress = deletionProgress,
            )
        }
    }

    // метод для удаления блюда прямо из меню, без таймера отмены
    private fun removeFromCartByMeal(meal: Meal) {
        _state.update { currentState ->
            val pendingDeletionItems = currentState.pendingDeletionMeals.toMutableList()
            val cartItems = currentState.cartItems
            val deletionProgress = currentState.mealDeletionProgress.toMutableMap()

            // Ищем последний добавленный CartItem с таким же meal.id
            val item = cartItems.keys.lastOrNull { it.meal.id == meal.id }

            if (item == null) {
                // Ничего не найдено — возвращаем текущее состояние
                return@update currentState
            }

            val currentQuantity = cartItems[item] ?: 0

            val updatedCartList = cartItems.toMutableMap().apply {
                // если в корзине  больше одной штуки item
                if (currentQuantity > 1) {
                    put(item, currentQuantity - 1)

                } else {
                    remove(item)
                    pendingDeletionItems.remove(item)
                    deletionProgress.entries.removeIf { it.key == item }
                }
            }
            cartInteractor.removeFromCart(item)
            currentState.copy(
                cartItems = updatedCartList,
                pendingDeletionMeals = pendingDeletionItems,
            )
        }
    }

    private fun updateCartState() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val cartItems = cartInteractor.getCart()

            _state.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    cartItems = cartItems,
                )
            }
            Log.d("DEBUG Cart", "CartViewModel - updateCartState, cartItems: $cartItems")
        }
    }

    private val clearCartDebounce = debounce<Unit>(
        CLEAR_CART_DEBOUNCE_DELAY,
        viewModelScope,
        useLastParam = true
    ) { _ ->
        clear()
    }

    private fun cancelClearingCart() {
        clearCartDebounce.cancel()
        cancelCartClearingTimer()
        _state.update {
            it.copy(isPendingDeletion = false, cartClearingProgress = null)
        }
    }

    private fun clear() {
        cartInteractor.clearCart()
        cancelAllMealTimers()
        _state.update {
            it.copy(
                cartItems = emptyMap(),
                isPendingDeletion = false,
                cartClearingProgress = null
            )
        }
        Log.d("DEBUG Cart", "CartViewModel - clear")
    }

// Для работы с таймерами удаления блюд и очистки корзины

    private fun startProgressTimer(item: CartItem? = null, duration: Long) {
        val interval = INTERVAL_FOR_UPD_PROGRESSBAR
        val steps = (duration / interval).toInt()

        if (item != null) {
            // Отменяем существующий таймер для этого блюда, если есть
            cancelMealDeletionTimer(item)

            val job = viewModelScope.launch {
                repeat(steps) { step ->
                    delay(interval)
                    val progress = step / steps.toFloat()
                    _state.update { state ->
                        state.copy(
                            mealDeletionProgress = state.mealDeletionProgress + (item to progress)
                        )
                    }
                }
                // По завершении удаляем таймер
                itemTimers.remove(item)
            }

            itemTimers[item] = job

        } else {
            // Общий таймер для очистки корзины
            cancelCartClearingTimer()
            clearCartTimerJob = viewModelScope.launch {
                _state.update { state ->
                    state.copy(cartClearingProgress = null)
                }
                repeat(steps) { step ->
                    delay(interval)
                    val progress = step / steps.toFloat()
                    _state.update { state ->
                        state.copy(cartClearingProgress = progress)
                    }
                }
            }
        }
    }

    private fun cancelMealDeletionTimer(item: CartItem) {
        itemTimers[item]?.cancel()
        itemTimers.remove(item)

        _state.update { state ->
            state.copy(
                mealDeletionProgress = state.mealDeletionProgress - item
            )
        }
    }

    private fun cancelCartClearingTimer() {
        clearCartTimerJob?.cancel()
        clearCartTimerJob = null

        _state.update { state ->
            state.copy(cartClearingProgress = null)
        }
    }

    private fun cancelAllMealTimers() {
        itemTimers.values.forEach { it.cancel() }
        itemTimers.clear()

        _state.update { state ->
            state.copy(
                mealDeletionProgress = emptyMap()
            )
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeCartChanges() {
        viewModelScope.launch {
            state
                .debounce(UPD_RECOMMEND_AFTER_CART_CHANGE_DEBOUNCE)
                .distinctUntilChangedBy { it.cartItems }
                .collect { currentState ->
                    updateRecommends(currentState.cartItems.keys)
                }
        }
    }

    private suspend fun updateRecommends(cartItems: Set<CartItem>) {
        val recommendsList = cartInteractor.getRecommends().map { it.toCartItem() }

        _state.update { state ->
            state.copy(
                recommendsList = recommendsList.filter { recommendItem ->
                    !cartItems.any { it.meal.id == recommendItem.meal.id }
                }
            )
        }
    }
}