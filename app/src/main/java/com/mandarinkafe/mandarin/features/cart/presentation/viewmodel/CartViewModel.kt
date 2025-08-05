package com.mandarinkafe.mandarin.features.cart.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.cart.domain.CartMapper.toCartItem
import com.mandarinkafe.mandarin.features.cart.domain.usecase.CartInteractor
import com.mandarinkafe.mandarin.features.cart.domain.usecase.GetAllRecommendsUseCase
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEffect
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.AddToCart
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.CancelRemove
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.ClearCart
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.ConfirmClearCart
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.Init
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.RemoveFromCartByCustomizedMeal
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.RemoveFromCartByMeal
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.RemoveFromCartWithDelay
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.ReplaceMealInCart
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartState
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
    private val itemTimers = mutableMapOf<CartItem, Job>()

    override fun onEvent(event: CartEvent) {
        when (event) {
            is Init -> {
                updateCartState()
                observeCartChanges()
            }

            is AddToCart -> addItem(item = event.item, customizedMeal = event.customizedMeal)
            is RemoveFromCartWithDelay -> onReduceItem(item = event.item)
            is RemoveFromCartByCustomizedMeal -> removeFromCartByMealOrCustomized(customizedMeal = event.item)
            is RemoveFromCartByMeal -> removeFromCartByMealOrCustomized(meal = event.meal)
            is CancelRemove -> cancelRemove(item = event.item)
            is ClearCart -> clearConfirmation()
            is ConfirmClearCart -> clear()
            is ReplaceMealInCart -> replaceMealInCart(
                newItem = event.newItem,
                oldItem = event.oldItem
            )

            is CartEvent.OnProceedOrderClick -> onProceedOrderClick()
            is CartEvent.AddCommentToItem -> setCommentToItem(event.item, event.comment)
        }
    }

    private fun setCommentToItem(item: CartItem, comment: String) {
        val newItem = item.copy(comment = comment)
        replaceMealInCart(
            newItem = newItem,
            oldItem = item
        )
    }

    /** Вызывает диалог для подтверждения желания очистить корзину */
    private fun clearConfirmation() {
        sendEffect(CartEffect.ShowClearCartConfirmDialog)
    }

    /** Вызывает эффект для перехода к оформлению заказа*/
    private fun onProceedOrderClick() {
        sendEffect(CartEffect.ProceedOrder)
    }

    /**  Заменяет в корзине отредактированное блюдо  */
    private fun replaceMealInCart(newItem: CartItem, oldItem: CartItem) {
        if (newItem == oldItem) return

        val index = state.value.cartItems.indexOfFirst { it.customizedMeal == oldItem }
        if (index == -1) return

        cartInteractor.removeFromCart(oldItem)
        cartInteractor.addToCart(newItem)

        setState {
            val updated = cartItems.toMutableList().apply {
                removeAt(index)
                add(index, newItem)
            }
            copy(cartItems = updated)
        }
    }

    private val removeDebounce = debounce<CartItem>(
        DELETE_FROM_CART_DEBOUNCE_DELAY,
        viewModelScope,
        useLastParam = true
    ) { item ->
        removeItem(item)
    }

    private fun addItem(item: CartItem? = null, customizedMeal: CustomizedMeal? = null) {
        val cartItem = when {
            item != null -> item
            customizedMeal != null -> customizedMeal.toCartItem()
            else -> return
        }
        Log.d(ERROR_TAG, "adding: $item / $customizedMeal")
        cartInteractor.addToCart(cartItem)

        setState {
            val existing = cartItems.find { it == cartItem }
            val updatedList = if (existing != null) {
                cartItems.map {
                    if (it == cartItem)
                        it.copy(quantity = it.quantity + 1)
                    else it
                }
            } else {
                cartItems + cartItem
            }
            copy(cartItems = updatedList)
        }
    }

    /** «–» нажато: если количество >1 — просто уменьшаем, иначе — запускаем отложенное удаление с таймером. */
    private fun onReduceItem(item: CartItem) {
        val cartItem = state.value.cartItems.find { it.customizedMeal == item } ?: return
        if (cartItem.quantity > 1) {
            reduceQuantity(item)
        } else {
            scheduleRemoval(item)
        }
    }

    /** Уменьшить количество без таймера. */
    private fun reduceQuantity(item: CartItem) {
        cartInteractor.removeFromCart(item)
        setState {
            val updatedList = cartItems.mapNotNull {
                if (it.customizedMeal == item) {
                    if (it.quantity > 1) it.copy(quantity = it.quantity - 1) else null
                } else it
            }
            copy(cartItems = updatedList)
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

    /** Окончательное удаление (вызов из debounce или из других экранов, если таймер на восстановление не нужен). */
    private fun removeItem(item: CartItem) {
        setState {
            val updatedCart = cartItems.filterNot { it.customizedMeal == item }
            cartInteractor.removeFromCart(item)
            copy(
                cartItems = updatedCart,
                pendingDeletionMeals = pendingDeletionMeals - item,
                mealDeletionProgress = mealDeletionProgress - item,
            )
        }
    }

    /**  Удаление без таймера, для вызова из экранов, где отображаются "базовые" блюда */
    private fun removeFromCartByMealOrCustomized(
        meal: Meal? = null,
        customizedMeal: CustomizedMeal? = null,
    ) {
        setState {
            val targetIndex = when {
                customizedMeal != null -> cartItems.indexOfLast { it.customizedMeal == customizedMeal }
                meal != null -> cartItems.indexOfLast { it.customizedMeal.meal.id == meal.id }
                else -> return@setState this
            }

            if (targetIndex == -1) return@setState this

            val targetItem = cartItems[targetIndex]
            val updatedCart = cartItems.toMutableList()
            val updatedPendingDeletion = pendingDeletionMeals.toMutableList()
            val updatedProgress = mealDeletionProgress.toMutableMap()

            if (targetItem.quantity > 1) {
                updatedCart[targetIndex] = targetItem.copy(quantity = targetItem.quantity - 1)
            } else {
                updatedCart.removeAt(targetIndex)
                updatedPendingDeletion.removeAll { it == targetItem }
                updatedProgress.entries.removeIf { it.key == targetItem }
            }

            cartInteractor.removeFromCart(targetItem)

            copy(
                cartItems = updatedCart,
                pendingDeletionMeals = updatedPendingDeletion,
                mealDeletionProgress = updatedProgress,
            )
        }
    }

    private fun updateCartState() {
        viewModelScope.launch {
            val cartResource = cartInteractor.getCart()
            setLoading(cartResource is Loading)

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
                cartItems = emptyList(),
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

    @OptIn(FlowPreview::class)
    private fun observeCartChanges() {
        viewModelScope.launch {
            state
                .debounce(UPD_RECOMMEND_AFTER_CART_CHANGE_DEBOUNCE)
                .distinctUntilChangedBy { it.cartItems }
                .collect { currentState ->
                    updateRecommends(currentState.cartItems.map { it.customizedMeal.meal }.toSet())
                }
        }
    }

    private suspend fun updateRecommends(cartItems: Set<Meal>) {
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

    private fun setData(data: List<CartItem>?) {
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
        val error = when (resource) {
            is Resource.ErrorEmptyData -> UiError.CartEmpty
            is Resource.ErrorNoInternet -> UiError.NoInternet
            is ErrorOther -> UiError.OtherError
            else -> return
        }
        setState { copy(error = error) }
    }

    private companion object {
        const val ERROR_TAG = "CartRepository VM"
        const val DELETE_FROM_CART_DEBOUNCE_DELAY: Long = 3000L
        const val INTERVAL_FOR_UPD_PROGRESSBAR: Long = 100L
        const val UPD_RECOMMEND_AFTER_CART_CHANGE_DEBOUNCE: Long = 500L
    }
}