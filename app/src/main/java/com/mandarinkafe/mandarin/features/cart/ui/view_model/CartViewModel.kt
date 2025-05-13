package com.mandarinkafe.mandarin.features.cart.ui.view_model

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.BaseViewModel
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.cart.CartMapper.toCartItem
import com.mandarinkafe.mandarin.features.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.features.cart.domain.usecase.CartInteractor
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract.CartEffect
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract.CartEffect.OpenMealDetailsBS
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract.CartEvent
import com.mandarinkafe.mandarin.util.Constants.CLEAR_CART_DEBOUNCE_DELAY
import com.mandarinkafe.mandarin.util.Constants.DELETE_FROM_CART_DEBOUNCE_DELAY
import com.mandarinkafe.mandarin.util.Constants.INTERVAL_FOR_UPD_PROGRESSBAR
import com.mandarinkafe.mandarin.util.Constants.UPD_RECOMMEND_AFTER_CART_CHANGE_DEBOUNCE
import com.mandarinkafe.mandarin.util.debounce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartInteractor: CartInteractor,
) : BaseViewModel<CartEvent, CartContract.CartState, CartEffect>() {
    override fun setInitialState() = CartContract.CartState()
    private val itemTimers = mutableMapOf<CartItem, Job>()
    private var clearCartTimerJob: Job? = null

    init {
        onEvent(CartEvent.GetCart)
        observeCartChanges()
    }

    override fun onEvent(event: CartEvent) {
        when (event) {
            CartEvent.GetCart -> updateCartState()
            is CartEvent.AddToCart -> addItem(item = event.item)
            is CartEvent.ReplaceMealInCart -> replaceMealInCart(
                newItem = event.newItem,
                oldItem = event.oldItem
            )

            is CartEvent.RemoveFromCartWithDelay -> onReduceItem(item = event.item)
            is CartEvent.RemoveFromCartByMeal -> removeFromCartByMeal(meal = event.meal)
            is CartEvent.CancelRemove -> cancelRemove(item = event.item)
            is CartEvent.ClearCart -> clearCartWithDebounce()
            is CartEvent.CancelClearingCart -> cancelClearingCart()
            is CartEvent.OpenMealDetails -> sendEffect(
                OpenMealDetailsBS(
                    item = event.item
                )
            )
        }
    }

    private fun replaceMealInCart(newItem: CartItem, oldItem: CartItem) {
        cartInteractor.removeFromCart(oldItem)
        cartInteractor.addToCart(newItem)

        setState {
            val updatedMap = cartItems.toMutableMap()
            val oldQuantity = updatedMap.remove(oldItem) ?: 1
            updatedMap[newItem] = oldQuantity

            copy(cartItems = updatedMap)
        }
    }

    private val removeDebounce = debounce<CartItem>(
        DELETE_FROM_CART_DEBOUNCE_DELAY,
        viewModelScope,
        useLastParam = true
    ) { item ->
        removeItem(item)
    }

    fun clearCartWithDebounce() {
        setState { copy(isPendingDeletion = true) }
        startProgressTimer(duration = CLEAR_CART_DEBOUNCE_DELAY)
        clearCartDebounce.invoke(Unit)
    }

    private fun addItem(item: CartItem) {
        cartInteractor.addToCart(item)

        setState {
            val cartItems = cartItems
            val currentQuantity = cartItems[item] ?: 0
            val newCartItems = cartItems.toMutableMap().apply {
                put(item, currentQuantity + 1)
            }
            copy(
                cartItems = newCartItems
            )
        }
    }

    // Обработка кнопки "-". Если в корзине была 1 шт - запуск отложенного удаления
    private fun onReduceItem(item: CartItem) {
        setState {
            val pendingDeletionItems = pendingDeletionMeals.toMutableList()
            val cartItems = cartItems
            val currentQuantity = cartItems[item] ?: 0
            val updatedCartList = cartItems.toMutableMap().apply {
                // если в корзине больше одной штуки item
                if (currentQuantity > 1) {
                    put(item, currentQuantity - 1)
                    cartInteractor.removeFromCart(item)
                } else {
                    removeDebounce.invoke(item)
                    startProgressTimer(item = item, duration = DELETE_FROM_CART_DEBOUNCE_DELAY)
                    pendingDeletionItems.add(item)
                }
            }

            copy(
                cartItems = updatedCartList,
                pendingDeletionMeals = pendingDeletionItems,
            )
        }
    }

    // отмена удаления
    private fun cancelRemove(item: CartItem) {
        removeDebounce.cancel()
        cancelMealDeletionTimer(item)

        setState {
            cartItems
            val updatedPendingDeletionItems =
                pendingDeletionMeals.toMutableList() - item
            val updatedDeletionProgress = mealDeletionProgress.toMutableMap()

            updatedDeletionProgress.entries.removeIf { it.key == item }

            copy(
                pendingDeletionMeals = updatedPendingDeletionItems,
                mealDeletionProgress = updatedDeletionProgress,
            )
        } as CartContract.CartState.() -> CartContract.CartState
    }

    // Окончательное удаление из корзины
    private fun removeItem(item: CartItem) {
        setState {
            val pendingDeletionItems = pendingDeletionMeals.toMutableList()
            val deletionProgress = mealDeletionProgress.toMutableMap()

            val updatedCartList = cartItems.toMutableMap().apply {
                remove(item)
            }

            pendingDeletionItems.remove(item)
            deletionProgress.entries.removeIf { it.key == item }
            cartInteractor.removeFromCart(item)

            copy(
                cartItems = updatedCartList,
                pendingDeletionMeals = pendingDeletionItems,
                mealDeletionProgress = deletionProgress,
            )
        }
    }

    // метод для удаления блюда прямо из меню, без таймера отмены
    private fun removeFromCartByMeal(meal: Meal) {
        setState {
            val pendingDeletionItems = pendingDeletionMeals.toMutableList()
            val deletionProgress = mealDeletionProgress.toMutableMap()

            // Ищем последний добавленный CartItem с таким же meal.id
            val item = cartItems.keys.lastOrNull { it.meal.id == meal.id }

            if (item == null) {
                // Ничего не найдено — возвращаем текущее состояние
                return@setState this
            }

            val currentQuantity = cartItems[item] ?: 0

            val updatedCartList = cartItems.toMutableMap().apply {
                if (currentQuantity > 1) {
                    put(item, currentQuantity - 1)
                } else {
                    remove(item)
                    pendingDeletionItems.remove(item)
                    deletionProgress.entries.removeIf { it.key == item }
                }
            }

            cartInteractor.removeFromCart(item)

            copy(
                cartItems = updatedCartList,
                pendingDeletionMeals = pendingDeletionItems,
                mealDeletionProgress = deletionProgress
            )
        }
    }

    private fun updateCartState() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            val cartItems = cartInteractor.getCart()

            setState {
                copy(
                    isLoading = false,
                    cartItems = cartItems,
                )
            }
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
        setState {
            copy(isPendingDeletion = false, cartClearingProgress = null)
        }
    }

    private fun clear() {
        cartInteractor.clearCart()
        cancelAllMealTimers()
        setState {
            copy(
                cartItems = emptyMap(),
                isPendingDeletion = false,
                cartClearingProgress = null
            )
        }
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
                    setState {
                        copy(
                            mealDeletionProgress = mealDeletionProgress + (item to progress)
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
                setState {
                    copy(cartClearingProgress = null)
                }
                repeat(steps) { step ->
                    delay(interval)
                    val progress = step / steps.toFloat()
                    setState {
                        copy(cartClearingProgress = progress)
                    }
                }
            }
        }
    }

    private fun cancelMealDeletionTimer(item: CartItem) {
        itemTimers[item]?.cancel()
        itemTimers.remove(item)

        setState {
            copy(
                mealDeletionProgress = mealDeletionProgress - item
            )
        }
    }

    private fun cancelCartClearingTimer() {
        clearCartTimerJob?.cancel()
        clearCartTimerJob = null
        setState {
            copy(
                cartClearingProgress = null
            )
        }

    }

    private fun cancelAllMealTimers() {
        itemTimers.values.forEach { it.cancel() }
        itemTimers.clear()
        setState {
            copy(
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

        setState {
            copy(
                recommendsList = recommendsList.filter { recommendItem ->
                    !cartItems.any { it.meal.id == recommendItem.meal.id }
                }
            )
        }
    }
}