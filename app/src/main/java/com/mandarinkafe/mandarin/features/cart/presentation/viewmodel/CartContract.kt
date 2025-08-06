package com.mandarinkafe.mandarin.features.cart.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.totalPrice
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState

sealed interface CartContract {
    sealed interface CartEvent : BaseEvent {
        // Управление элементами корзины
        data class AddToCart(
            val item: CartItem? = null,
            val customizedMeal: CustomizedMeal? = null
        ) : CartEvent

        data class AddCommentToItem(val item: CartItem, val comment: String) : CartEvent
        data class UpdateMealInCart(val newItem: CartItem) : CartEvent
        data class OnReduceWithDelay(val item: CartItem) : CartEvent
        data class CancelRemove(val item: CartItem) : CartEvent
        data class OnReduce(
            val customizedMeal: CustomizedMeal? = null,
            val meal: Meal? = null
        ) : CartEvent

        // Очистка корзины
        data object ClearCart : CartEvent
        data object ConfirmClearCart : CartEvent

        // Переход к оформлению заказа
        data object OnProceedOrderClick : CartEvent
    }

    sealed interface CartEffect : BaseEffect {
        data object ProceedOrder : CartEffect
        data object ShowClearCartConfirmDialog : CartEffect
    }

    data class CartState(
        val isLoading: Boolean = true,
        val error: UiError? = null,
        val cartItems: List<CartItem> = emptyList(),
        val favoritesItems: Set<CustomizedMeal> = emptySet(),
        val recommends: List<Meal> = emptyList(),
        val recommendsAreLoading: Boolean = true,
        val pendingDeletionMeals: List<CartItem> = emptyList(),
        val mealDeletionProgress: Map<CartItem, Float> = emptyMap(),
    ) : BaseState {
        val actualCartItems: List<CartItem>
            get() = cartItems.filter { it !in pendingDeletionMeals }

        val totalCartPrice: Int
            get() = actualCartItems.sumOf { it.customizedMeal.totalPrice() * it.quantity }
    }
}