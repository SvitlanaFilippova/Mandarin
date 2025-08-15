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
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.UpdateMealInCart
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartState
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.CheckIfTerminalIsAliveUseCase
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorOther
import com.mandarinkafe.mandarin.util.Resource.Loading
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
    private val checkIfTerminalIsAlive: CheckIfTerminalIsAliveUseCase
) : BaseViewModel<CartEvent, CartEffect, CartState>() {
    override fun setInitialState() = CartState()
    private val itemTimers = mutableMapOf<CartItem, Job>()
    private val itemRemoveJobs = mutableMapOf<CartItem, Job>()

    init {
        observeCartChanges()
    }

    private val logTag = "CART DEBUG VM"

    override fun onEvent(event: CartEvent) {
        when (event) {
            is AddToCart -> addItem(item = event.item, customizedMeal = event.customizedMeal)
            is OnReduceWithDelay -> onReduceItemWithDelay(item = event.item)
            is OnReduce -> removeItem(meal = event.meal, customizedMeal = event.customizedMeal)
            is CancelRemove -> cancelRemove(item = event.item)
            is ClearCart -> clearConfirmation()
            is ConfirmClearCart -> clear()
            is UpdateMealInCart -> updateMealInCart(item = event.newItem)
            is CartEvent.OnProceedOrderClick -> onProceedOrderClick()
            is CartEvent.AddCommentToItem -> setCommentToItem(event.item, event.comment)
            is CartEvent.ForceRefresh -> forceRefresh()
        }
    }

    private fun forceRefresh() {
        viewModelScope.launch {
            cartInteractor.forceRetry()
            updateRecommends(state.value.cartItems.map { it.customizedMeal.meal }.toSet())
        }
    }

    private fun observeCartChanges() {
        viewModelScope.launch {
            cartInteractor.observeCartItems().collect { proceedCartResult(it) }
        }
    }

    private suspend fun proceedCartResult(cartResource: Resource<List<CartItem>>) {
        Log.d(logTag, "observeCartItems emitted: $cartResource")
        when (cartResource) {
            is Resource.Success -> {
                Log.d(logTag, "Cart updated: ${cartResource.data?.size} items")
                setData(cartResource.data)
                updateRecommends(cartResource.data?.map { it.customizedMeal.meal }?.toSet())
            }

            is Loading -> {
                Log.d(logTag, "Loading cart data")
                setLoading()
            }

            is Resource.ErrorNoInternet -> {
                if (state.value.cartItems.isEmpty()) {
                    setError(cartResource)
                } else {
                    Log.w(logTag, "No internet, but cart already has items, skip error")
                    setLoading(false)
                }
            }

            is Resource.Idle -> {}
            else -> {
                Log.e(logTag, "Error loading cart: $cartResource")
                setError(cartResource)
            }
        }
    }

    private fun onReduceItemWithDelay(item: CartItem) {
        if (item.quantity > 1) {
            reduceQuantity(item)
        } else {
            scheduleRemoval(item)
        }
    }

    private fun reduceQuantity(item: CartItem) {
        val currentQuantity = item.quantity
        val id = item.id
        setState { copy(inProgressItems = inProgressItems + id) }
        viewModelScope.launch {
            cartInteractor.updateItem(item.copy(quantity = currentQuantity - 1))
        }
    }

    private fun scheduleRemoval(item: CartItem) {
        // Если уже есть таймер — не дублируем
        if (itemRemoveJobs.containsKey(item)) return

        val job = viewModelScope.launch {
            delay(DELETE_FROM_CART_DEBOUNCE_DELAY)
            removeItem(item)
            itemRemoveJobs.remove(item)
        }

        itemRemoveJobs[item] = job
        startProgressTimer(item)

        setState {
            copy(pendingDeletionItems = pendingDeletionItems + item.id)
        }
    }

    private fun cancelRemove(item: CartItem) {
        itemRemoveJobs[item]?.cancel()
        itemRemoveJobs.remove(item)
        cancelMealDeletionTimer(item)

        setState {
            val updatedPendingDeletionItems = pendingDeletionItems.toMutableList() - item.id
            val updatedDeletionProgress = mealDeletionProgress.toMutableMap()
            updatedDeletionProgress.entries.removeIf { it.key == item.id }
            copy(
                pendingDeletionItems = updatedPendingDeletionItems,
                mealDeletionProgress = updatedDeletionProgress,
            )
        }
    }

    private fun removeItem(
        item: CartItem? = null,
        meal: Meal? = null,
        customizedMeal: CustomizedMeal? = null,
    ) {
        val mealId =
            item?.id ?: customizedMeal?.meal?.id ?: meal?.id ?: return
        setState { copy(inProgressItems = inProgressItems + mealId) }
        viewModelScope.launch {
            cartInteractor.removeFromCart(
                cartItemId = item?.id,
                customizedMeal = customizedMeal,
                meal = meal
            )
            delay(REMOVE_FROM_PENDING_DELETION_DELAY)
            item?.let {
                setState {
                    copy(
                        pendingDeletionItems = pendingDeletionItems - item.id,
                        mealDeletionProgress = mealDeletionProgress - item.id,
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

    private fun clearConfirmation() {
        sendEffect(CartEffect.ShowClearCartConfirmDialog)
    }

    private fun onProceedOrderClick() {
        viewModelScope.launch {
            setState { copy(proceedOrderIsLoading = true) }
            val terminalResponse = checkIfTerminalIsAlive()

            setState { copy(proceedOrderIsLoading = false) }
            when (terminalResponse) {
                is Resource.Success -> {
                    if (terminalResponse.data == true) {
                        sendEffect(CartEffect.ProceedOrder)
                    } else {
                        sendEffect(
                            CartEffect.ShowSnackbar(
                                "Упс, кажется, сейчас мы не работаем — заказ оформить не получится :(. Возвращайся в рабочее время!"
                            )
                        )
                    }
                }

                is Resource.ErrorNoInternet -> {
                    sendEffect(
                        CartEffect.ShowSnackbar(
                            "Нет подключения к интернету."
                        )
                    )
                }

                else -> sendEffect(
                    CartEffect.ShowSnackbar(
                        "Что-то пошло не так — не удалось проверить, работает ли сейчас доставка."
                    )
                )
            }
        }
    }

    private fun setCommentToItem(item: CartItem, comment: String) {
        val newItem = item.copy(comment = comment)
        viewModelScope.launch {
            cartInteractor.updateItem(cartItem = newItem)
        }
    }

    private fun updateMealInCart(item: CartItem) {
        Log.d("Cart DEBUG VM", "call updateMealInCart, item: $item ")
        val id = item.id
        setState { copy(inProgressItems = inProgressItems + id) }
        viewModelScope.launch {
            cartInteractor.updateItem(cartItem = item)
        }
    }

    private fun addItem(
        item: CartItem? = null,
        customizedMeal: CustomizedMeal? = null,
        meal: Meal? = null
    ) {
        Log.d("Cart DEBUG VM", "call addItem, item: $item ")
        val tempId =
            item?.id ?: customizedMeal?.meal?.id ?: meal?.id ?: return
        setState {
            copy(inProgressItems = inProgressItems + tempId)
        }
        viewModelScope.launch {
            cartInteractor.addItem(cartItem = item, customizedMeal = customizedMeal, meal = meal)
        }
    }

    private fun startProgressTimer(item: CartItem) {
        val interval = INTERVAL_FOR_UPD_PROGRESSBAR
        val steps = (DELETE_FROM_CART_DEBOUNCE_DELAY / interval).toInt()
        cancelMealDeletionTimer(item)
        val job = viewModelScope.launch {
            repeat(steps) { step ->
                delay(interval)
                val progress = step / steps.toFloat()
                setState {
                    copy(
                        mealDeletionProgress = mealDeletionProgress + (item.id to progress)
                    )
                }
            }
            itemTimers.remove(item)
        }
        itemTimers[item] = job
    }

    private fun cancelMealDeletionTimer(item: CartItem) {
        itemTimers[item]?.cancel()
        itemTimers.remove(item)
        setState {
            copy(
                mealDeletionProgress = mealDeletionProgress - item.id
            )
        }
    }

    private fun cancelAllMealTimers() {
        itemTimers.values.forEach { it.cancel() }
        itemTimers.clear()
        itemRemoveJobs.values.forEach { it.cancel() }
        itemRemoveJobs.clear()
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
                    isLoading = false,
                    inProgressItems = emptySet()
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
        val hasItems = state.value.cartItems.isNotEmpty()
        setLoading(false)

        val error = when (resource) {
            is Resource.ErrorEmptyData -> UiError.CartEmpty
            is Resource.ErrorNoInternet -> if (hasItems) null else UiError.NoInternet
            is ErrorOther -> UiError.OtherError
            else -> return
        }

        if (error != null) {
            setState { copy(error = error, isLoading = false) }
        }
    }

    private companion object {
        const val REMOVE_FROM_PENDING_DELETION_DELAY: Long = 1000L
        const val DELETE_FROM_CART_DEBOUNCE_DELAY: Long = 3000L
        const val INTERVAL_FOR_UPD_PROGRESSBAR: Long = 100L
    }
}
