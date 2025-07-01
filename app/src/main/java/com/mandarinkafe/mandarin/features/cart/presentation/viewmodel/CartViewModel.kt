package com.mandarinkafe.mandarin.features.cart.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Mapper.toCustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.cart.domain.usecase.CartInteractor
import com.mandarinkafe.mandarin.features.cart.domain.usecase.GetAllRecommendsUseCase
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEffect
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.AddToCart
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.CancelRemove
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.ClearCart
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.ConfirmClearCart
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.Init
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.RemoveFromCartByItem
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.RemoveFromCartByMeal
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.RemoveFromCartWithDelay
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.ReplaceMealInCart
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartState
import com.mandarinkafe.mandarin.util.Constants.DELETE_FROM_CART_DEBOUNCE_DELAY
import com.mandarinkafe.mandarin.util.Constants.INTERVAL_FOR_UPD_PROGRESSBAR
import com.mandarinkafe.mandarin.util.Constants.UPD_RECOMMEND_AFTER_CART_CHANGE_DEBOUNCE
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorOther
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.debounce
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
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
    private val recommendsUseCase: GetAllRecommendsUseCase,
) : BaseViewModel<CartEvent, CartEffect, CartState>() {
    override fun setInitialState() = CartState()
    private val itemTimers = mutableMapOf<CustomizedMeal, Job>()

    override fun onEvent(event: CartEvent) {
        when (event) {
            is Init -> {
                updateCartState()
                observeCartChanges()
            }

            is AddToCart -> addItem(item = event.item)
            is RemoveFromCartWithDelay -> onReduceItem(item = event.item)
            is RemoveFromCartByItem -> removeItem(item = event.item)
            is RemoveFromCartByMeal -> removeFromCartByMeal(meal = event.meal)
            is CancelRemove -> cancelRemove(item = event.item)
            is ClearCart -> clearConfirmation()
            is ConfirmClearCart -> clear()
            is ReplaceMealInCart -> replaceMealInCart(
                newItem = event.newItem,
                oldItem = event.oldItem
            )
        }
    }

    /**
     * Вызывает диалог для подтверждения желания очистить корзину
     */
    private fun clearConfirmation() {
        sendEffect(CartEffect.ShowClearCartConfirmationDialog)
    }

    /**
     * Заменяет в корзине отредактированное блюдо
     */
    private fun replaceMealInCart(newItem: CustomizedMeal, oldItem: CustomizedMeal) {
        if (newItem == oldItem) return
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

    /**
     * «–» нажато: если количество >1 — просто уменьшаем,
     * иначе — запускаем отложенное удаление с таймером.
     */
    private fun onReduceItem(item: CustomizedMeal) {
        val currentQty = state.value.cartItems[item] ?: 0
        if (currentQty > 1) {
            // уменьшить сразу на единицу
            reduceQuantity(item)
        } else {
            // запланировать удаление
            scheduleRemoval(item)
        }
    }

    /** Уменьшить количество без таймера. */
    private fun reduceQuantity(item: CustomizedMeal) {
        cartInteractor.removeFromCart(item)
        setState {
            val updated = cartItems.toMutableMap().apply {
                put(item, (get(item) ?: 0) - 1)
            }
            copy(cartItems = updated)
        }
    }

    /** Запускает отложенное удаление с debounce и прогрессом. */
    private fun scheduleRemoval(item: CustomizedMeal) {
        // ставим «в ожидании» и запускаем дебаунс
        removeDebounce.invoke(item)
        startProgressTimer(item)

        setState {
            copy(
                pendingDeletionMeals = pendingDeletionMeals + item
            )
        }
    }

    /** Отменяет отложенное удаление (и убирает прогресс). */
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

    /** Окончательное удаление (вызов из debounce или из других экранов, если таймер на восстановление не нужен). */
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

    /**
    Удаление без таймера, для вызова из экранов, где отображаются "базовые" блюда
     */
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
            val cartResource = cartInteractor.getCart()
            setLoading(cartResource is Loading)
            Log.e(
                "DEBUG EMPTY CART",
                "CartViewModel, updateCartState cartResource: $cartResource",
            )
            when (cartResource) {
                is Resource.Success -> setData(cartResource.data)
                is Resource.Idle -> {}
                is Loading -> {}
                else -> setError(cartResource)
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

    // Для работы с таймерами удаления блюд
    private fun startProgressTimer(item: CustomizedMeal) {
        val interval = INTERVAL_FOR_UPD_PROGRESSBAR
        val steps = (DELETE_FROM_CART_DEBOUNCE_DELAY / interval).toInt()

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
        val currentCartMeals: Set<Meal> = cartItems.map { it.meal }.toSet()
        val resource = recommendsUseCase(currentCartMeals)
        setRecommendsLoading(resource is Loading)
        val filteredRecommends =
            when (resource) {
                is Resource.Success -> resource.data ?: emptyList()
                else -> emptyList()
            }
        setState {
            copy(recommends = filteredRecommends.toList().map { it.toCustomizedMeal() })
        }
    }

    private fun setData(data: Map<CustomizedMeal, Int>?) {
        Log.d("DEBUG EMPTY CART", "CartViewModel. setData, data: $data")
        if (!data.isNullOrEmpty()) {
            setState {
                copy(
                    cartItems = data,
                    error = null
                )
            }
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }

    private fun setRecommendsLoading(isLoading: Boolean) {
        setState { copy(recommendsAreLoading = isLoading) }
    }

    private fun setError(resource: Resource<*>) {
        Log.d("DEBUG EMPTY CART", "CartViewModel. setError, resource: $resource")
        val error = when (resource) {
            is Resource.ErrorEmptyData -> UiError.CartEmpty
            is Resource.ErrorNoInternet -> UiError.NoInternet
            is ErrorOther -> UiError.OtherError
            else -> return
        }
        setState { copy(error = error) }
    }
}