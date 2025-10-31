package com.mandarinkafe.mandarin.features.cart.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.cart.domain.api.CartInteractor
import com.mandarinkafe.mandarin.features.cart.domain.api.GetRecommendsUseCase
import com.mandarinkafe.mandarin.features.cart.domain.models.Recommends
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEffect
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEffect.ShowSnackbar
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.AddCommentToItem
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.AddToCart
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.CancelRemove
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.ClearCart
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.ConfirmClearCart
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.ForceRefresh
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.OnProceedOrderClick
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.OnReduce
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent.OnReduceWithDelay
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartState
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.CheckIfTerminalIsAliveUseCase
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorEmptyData
import com.mandarinkafe.mandarin.util.Resource.ErrorNoInternet
import com.mandarinkafe.mandarin.util.Resource.ErrorOther
import com.mandarinkafe.mandarin.util.Resource.Idle
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.Resource.Success
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartInteractor: CartInteractor,
    private val recommendsUseCase: GetRecommendsUseCase,
    private val checkIfTerminalIsAlive: CheckIfTerminalIsAliveUseCase,
) : BaseViewModel<CartEvent, CartEffect, CartState>() {
    override fun setInitialState() = CartState()
    private val itemTimers = mutableMapOf<CartItem, Job>()
    private val itemRemoveJobs = mutableMapOf<CartItem, Job>()

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
            is OnProceedOrderClick -> onProceedOrderClick()
            is AddCommentToItem -> setCommentToItem(event.item, event.comment)
            is ForceRefresh -> forceRefresh()
        }
    }

    private fun forceRefresh() {
        viewModelScope.launch {
            setLoading()
            resetError()
            cartInteractor.forceRefresh()
            updateRecommends(state.value.cartItems.map { it.customizedMeal.meal }.toSet())
        }
    }

    private fun observeCartChanges() {
        viewModelScope.launch {
            cartInteractor.observeCartItems().collect { proceedCartResult(it) }
        }
    }

    private suspend fun proceedCartResult(resource: Resource<List<CartItem>>) {
        when (resource) {
            is Success -> {
                setData(resource.data)
                updateRecommends(resource.data?.map { it.customizedMeal.meal }?.toSet())
            }

            is Loading, is Idle -> {
                setLoading()
            }

            is ErrorNoInternet -> {
                if (state.value.cartItems.isEmpty()) {
                    setError(resource)
                } else {
                    setLoading(false)
                }
            }

            else -> {
                setError(resource)
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
            updatedDeletionProgress.entries.removeAll { it.key == item.id }
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
                is Success -> {
                    if (terminalResponse.data == true) {
                        sendEffect(CartEffect.ProceedOrder)
                    } else {
                        showSnackbar(
                            message = MR.strings.terminal_unavailable,
                        )
                    }
                }

                is ErrorNoInternet -> showSnackbar(MR.strings.error_no_internet)

                else -> showSnackbar(MR.strings.check_terminal_failed)
            }
        }
    }

    private fun showSnackbar(
        message: dev.icerock.moko.resources.StringResource,
        showToCartButton: Boolean = false,
    ) {
        sendEffect(ShowSnackbar(message, showToCartButton))
    }

    private fun setCommentToItem(item: CartItem, comment: String) {
        val newItem = item.copy(comment = comment)
        viewModelScope.launch {
            cartInteractor.updateItem(newCartItem = newItem)
        }
    }

    private fun addItem(
        item: CartItem? = null,
        customizedMeal: CustomizedMeal? = null,
        meal: Meal? = null,
    ) {
        val tempId =
            item?.id ?: customizedMeal?.meal?.id ?: meal?.id ?: return
        setState {
            copy(inProgressItems = inProgressItems + tempId)
        }
        viewModelScope.launch {
            cartInteractor.addItem(
                cartItem = item,
                customizedMeal = customizedMeal,
                meal = meal
            )
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
            val recommends = resource.data ?: Recommends()
            setState {
                copy(recommends = recommends)
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
                    inProgressItems = emptySet(),
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
        setLoading(false)

        val hasItems = state.value.cartItems.isNotEmpty()

        val error = when (resource) {
            is ErrorEmptyData -> UiError.CartEmpty
            is ErrorNoInternet -> if (hasItems) null else UiError.NoInternet
            is ErrorOther -> UiError.OtherError
            else -> return
        }
        setState { copy(error = error) }
    }

    private fun resetError() {
        setState { copy(error = null) }
    }

    private companion object {
        const val REMOVE_FROM_PENDING_DELETION_DELAY: Long = 1000L
        const val DELETE_FROM_CART_DEBOUNCE_DELAY: Long = 3000L
        const val INTERVAL_FOR_UPD_PROGRESSBAR: Long = 100L
    }
}

