package com.mandarinkafe.mandarin.features.cart.ui.view_model

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.BaseViewModel
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.cart.CartMapper.toCartItem
import com.mandarinkafe.mandarin.features.cart.domain.usecase.CartInteractor
import com.mandarinkafe.mandarin.features.cart.domain.usecase.GetRecommendsUseCase
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract.CartEffect
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract.CartEffect.OpenMealDetailsBS
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract.CartEvent
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract.CartState
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
    private val getRecommendsUseCase: GetRecommendsUseCase
) : BaseViewModel<CartEvent, CartEffect, CartState>() {
    override fun setInitialState() = CartState()
    private val itemTimers = mutableMapOf<CustomizedMeal, Job>()

    init {
        updateCartState()
        observeCartChanges()
    }

    override fun onEvent(event: CartEvent) {
        when (event) {
            is CartEvent.AddToCart -> addItem(item = event.item)
            is CartEvent.RemoveFromCartWithDelay -> onReduceItem(item = event.item)
            is CartEvent.RemoveFromCartByMeal -> removeFromCartByMeal(meal = event.meal)
            is CartEvent.CancelRemove -> cancelRemove(item = event.item)
            is CartEvent.ClearCart -> clearConfirmation()
            is CartEvent.ConfirmClearCart -> clear()
            is CartEvent.ReplaceMealInCart -> replaceMealInCart(
                newItem = event.newItem,
                oldItem = event.oldItem
            )

            is CartEvent.OpenMealDetails -> sendEffect(
                OpenMealDetailsBS(
                    item = event.item
                )
            )
        }
    }

    private fun clearConfirmation() {
        sendEffect(CartEffect.ShowClearCartConfirmationDialog)
    }

    private fun replaceMealInCart(newItem: CustomizedMeal, oldItem: CustomizedMeal) {
        cartInteractor.removeFromCart(oldItem)
        cartInteractor.addToCart(newItem)

        setState {
            val updatedMap = cartItems.toMutableMap()
            val oldQuantity = updatedMap.remove(oldItem) ?: 1
            updatedMap[newItem] = oldQuantity

            copy(cartItems = updatedMap)
        }
    }

    private val removeDebounce = debounce<CustomizedMeal>(
        DELETE_FROM_CART_DEBOUNCE_DELAY,
        viewModelScope,
        useLastParam = true
    ) { item ->
        removeItem(item)
    }

    private fun addItem(item: CustomizedMeal) {
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
    private fun onReduceItem(item: CustomizedMeal) {
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
    private fun cancelRemove(item: CustomizedMeal) {
        removeDebounce.cancel()
        cancelMealDeletionTimer(item)

        setState {
            val updatedPendingDeletionItems = pendingDeletionMeals.toMutableList() - item
            val updatedDeletionProgress = mealDeletionProgress.toMutableMap()

            updatedDeletionProgress.entries.removeIf { it.key == item }

            copy(
                pendingDeletionMeals = updatedPendingDeletionItems,
                mealDeletionProgress = updatedDeletionProgress,
            )
        }
    }

    // Окончательное удаление из корзины
    private fun removeItem(item: CustomizedMeal) {
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

    private fun clear() {
        cartInteractor.clearCart()
        cancelAllMealTimers()
        setState {
            copy(
                cartItems = emptyMap(),
            )
        }
    }

// Для работы с таймерами удаления блюд и очистки корзины

    private fun startProgressTimer(item: CustomizedMeal, duration: Long) {
        val interval = INTERVAL_FOR_UPD_PROGRESSBAR
        val steps = (duration / interval).toInt()

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

    }

    private fun cancelMealDeletionTimer(item: CustomizedMeal) {
        itemTimers[item]?.cancel()
        itemTimers.remove(item)

        setState {
            copy(
                mealDeletionProgress = mealDeletionProgress - item
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

    private suspend fun updateRecommends(cartItems: Set<CustomizedMeal>) {
        // Общие рекомендации
        val commonRecommends: Set<Meal> = cartInteractor
            .getCommonRecommends()
            .toSet()

        // Текущие блюда в корзине
        val currentCartMeals: Set<Meal> = cartItems
            .map { it.meal }
            .toSet()

        // Рекомендации по корзине
        val cartRecommends = getRecommendsUseCase(currentCartMeals)

        // Объединяем оба сета (union — без дубликатов)
        val allRecommends = cartRecommends union commonRecommends

        //  Фильтруем, убирая те, что уже в корзине
        val filteredRecommends: Set<Meal> = allRecommends
            .filter { recommend ->
                currentCartMeals.none { it.id == recommend.id }
            }
            .toSet()

        setState {
            copy(recommends = filteredRecommends.toList().map { it.toCartItem() })
        }
    }

}