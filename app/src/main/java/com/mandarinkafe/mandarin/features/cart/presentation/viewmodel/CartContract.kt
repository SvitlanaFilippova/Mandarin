package com.mandarinkafe.mandarin.features.cart.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.totalPrice
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState

sealed interface CartContract {
    sealed interface CartEvent : BaseEvent {
        // Инициализация
        data object Init : CartEvent

        // Управление элементами корзины
        data class AddToCart(val item: CustomizedMeal) : CartEvent
        data class RemoveFromCartWithDelay(val item: CustomizedMeal) : CartEvent
        data class RemoveFromCartByItem(val item: CustomizedMeal) : CartEvent
        data class RemoveFromCartByMeal(val meal: Meal) : CartEvent
        data class ReplaceMealInCart(val newItem: CustomizedMeal, val oldItem: CustomizedMeal) :
            CartEvent
        data class CancelRemove(val item: CustomizedMeal) : CartEvent

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
        val cartItems: Map<CustomizedMeal, Int> = emptyMap(),
        val favoritesItems: Set<CustomizedMeal> = emptySet(),
        val recommends: List<CustomizedMeal> = emptyList(),
        val recommendsAreLoading: Boolean = true,
        val pendingDeletionMeals: List<CustomizedMeal> = emptyList(),
        val mealDeletionProgress: Map<CustomizedMeal, Float> = emptyMap(),
    ) : BaseState {
        val actualCartItems: Map<CustomizedMeal, Int>
            get() = cartItems.filter { (item, _) -> item !in pendingDeletionMeals }

        val totalCartPrice: Int
            get() = actualCartItems.entries.sumOf { (item, quantity) ->
                item.totalPrice() * quantity
            }
    }
}