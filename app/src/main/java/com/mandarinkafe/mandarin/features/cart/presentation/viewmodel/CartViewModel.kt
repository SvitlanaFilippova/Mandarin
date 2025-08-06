package com.mandarinkafe.mandarin.features.cart.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
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
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.OnReduce
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.OnReduceWithDelay
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.ReplaceMealInCart
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartState
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorOther
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.debounce
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartInteractor: CartInteractor,
    private val recommendsUseCase: GetAllRecommendsUseCase,
) : BaseViewModel<CartEvent, CartEffect, CartState>() {
    override fun setInitialState() = CartState()
    private val itemTimers = mutableMapOf<CartItem, Job>()

    private val setCommentDebounce = debounce<Pair<CartItem, String>>(
        DEBOUNCE_FOR_COMMENT_DELAY,
        viewModelScope,
        useLastParam = true
    ) {
        setCommentToItem(it.first, it.second)
    }

    private val removeDebounce = debounce<CartItem>(
        DELETE_FROM_CART_DEBOUNCE_DELAY,
        viewModelScope,
        useLastParam = true
    ) { item ->
        removeItem(item)
    }

    init {
        observeCartChanges()
    }

    override fun onEvent(event: CartEvent) {
        when (event) {
            is AddToCart -> addItem(item = event.item, customizedMeal = event.customizedMeal)
            is OnReduceWithDelay -> onReduceItemWithDelay(item = event.item)
            is OnReduce -> removeItem(meal = event.meal, customizedMeal = event.customizedMeal)
            is CancelRemove -> cancelRemove(item = event.item)
            is ClearCart -> clearConfirmation()
            is ConfirmClearCart -> clear()
            is ReplaceMealInCart -> replaceMealInCart(
                newItem = event.newItem,
                oldItem = event.oldItem
            )

            is CartEvent.OnProceedOrderClick -> onProceedOrderClick()
            is CartEvent.AddCommentToItem -> setCommentWithDebounce(event.item, event.comment)
        }
    }

    private fun observeCartChanges() {
        viewModelScope.launch {
            cartInteractor.observeCartItems().collect { cartResource ->
                when (cartResource) {
                    is Resource.Success -> {
                        setData(cartResource.data)
                        updateRecommends(cartResource.data?.map { it.customizedMeal.meal }?.toSet())
                    }
                    is Loading -> setLoading()
                    is Resource.Idle -> {}
                    else -> setError(cartResource)
                }
            }
        }
    }

    /** «–» нажато: если количество >1 — просто уменьшаем, иначе — запускаем отложенное удаление с таймером. */
    private fun onReduceItemWithDelay(item: CartItem) {
        if (item.quantity > 1) {
            reduceQuantity(item)
        } else {
            scheduleRemoval(item)
        }
    }

    /** Уменьшить количество без таймера. */
    private fun reduceQuantity(item: CartItem) {
        val currentQuantity = item.quantity
        viewModelScope.launch {
            cartInteractor.updateItem(item.copy(quantity = currentQuantity - 1))
        }
    }

    /** Запускает отложенное удаление с debounce и прогрессом. */
    private fun scheduleRemoval(item: CartItem) {
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
    private fun cancelRemove(item: CartItem) {
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

    /** Уменьшение количества
     *  ИЛИ окончательное удаление (вызов из debounce или из других экранов, если таймер на восстановление не нужен). */
    private fun removeItem(
        item: CartItem? = null,
        meal: Meal? = null,
        customizedMeal: CustomizedMeal? = null,
    ) {
        viewModelScope.launch {
            cartInteractor.removeFromCart(
                cartItem = item,
                customizedMeal = customizedMeal,
                meal = meal
            )
            item?.let {
                setState {
                    copy(
                        pendingDeletionMeals = pendingDeletionMeals - item,
                        mealDeletionProgress = mealDeletionProgress - item,
                    )
                }
            }
        }
    }

    private fun clear() {
        viewModelScope.launch {
            cartInteractor.clearCart()
            cancelAllMealTimers()
        }
    }

    private fun setCommentWithDebounce(item: CartItem, comment: String) {
        setCommentDebounce.cancel()
        setCommentDebounce.invoke(Pair(item, comment))
    }

    /** Вызывает диалог для подтверждения желания очистить корзину */
    private fun clearConfirmation() {
        sendEffect(CartEffect.ShowClearCartConfirmDialog)
    }

    /** Вызывает эффект для перехода к оформлению заказа*/
    private fun onProceedOrderClick() {
        sendEffect(CartEffect.ProceedOrder)
    }

    private fun setCommentToItem(item: CartItem, comment: String) {
        val newItem = item.copy(comment = comment)
        replaceMealInCart(
            newItem = newItem,
            oldItem = item
        )
    }

    /**  Заменяет в корзине отредактированное блюдо  */
    private fun replaceMealInCart(newItem: CartItem, oldItem: CartItem) {
        if (newItem == oldItem) return
        viewModelScope.launch {
            cartInteractor.updateItem(cartItem = newItem)
        }
    }

    private fun addItem(
        item: CartItem? = null,
        customizedMeal: CustomizedMeal? = null,
        meal: Meal? = null
    ) {

        Log.d(ERROR_TAG, "adding: $item / $customizedMeal / $meal")
        viewModelScope.launch {
            cartInteractor.addItem(
                cartItem = item,
                customizedMeal = customizedMeal,
                meal = meal
            )
        }
    }

    // Для работы с таймерами удаления блюд
    private fun startProgressTimer(item: CartItem) {
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

    private fun cancelMealDeletionTimer(item: CartItem) {
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

    private suspend fun updateRecommends(cartItems: Set<Meal>?) {
        cartItems?.let {
            val resource = recommendsUseCase(cartItems)
            setRecommendsLoading(resource is Loading)
            val filteredRecommends =
                when (resource) {
                    is Resource.Success -> resource.data ?: emptyList()
                    else -> emptyList()
                }
            setState {
                copy(recommends = filteredRecommends)
            }
        }
    }

    private fun setData(data: List<CartItem>?) {
        data?.let {
            setState {
                copy(
                    cartItems = data,
                    error = null,
                    isLoading = false
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
        val error = when (resource) {
            is Resource.ErrorEmptyData -> UiError.CartEmpty
            is Resource.ErrorNoInternet -> UiError.NoInternet
            is ErrorOther -> UiError.OtherError
            else -> return
        }
        setState { copy(error = error, isLoading = false) }
    }

    private companion object {
        const val ERROR_TAG = "Cart DEBUG VM"
        const val DEBOUNCE_FOR_COMMENT_DELAY = 1000L
        const val DELETE_FROM_CART_DEBOUNCE_DELAY: Long = 3000L
        const val INTERVAL_FOR_UPD_PROGRESSBAR: Long = 100L
    }
}