package com.mandarinkafe.mandarin.features.cart.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.totalPrice
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.cart.domain.models.Recommends
import com.mandarinkafe.mandarin.util.presentation.BaseContract
import dev.icerock.moko.resources.StringResource

sealed interface CartContract {
    sealed interface CartEvent : BaseContract.BaseEvent {
        // Управление элементами корзины
        data class AddToCart(
            val item: CartItem? = null,
            val customizedMeal: CustomizedMeal? = null,
        ) : CartEvent

        data class AddCommentToItem(val item: CartItem, val comment: String) : CartEvent
        data class OnReduceWithDelay(val item: CartItem) : CartEvent
        data class CancelRemove(val item: CartItem) : CartEvent
        data class OnReduce(
            val customizedMeal: CustomizedMeal? = null,
            val meal: Meal? = null,
        ) : CartEvent

        data object ForceRefresh : CartEvent
        data object SyncWithRemote : CartEvent

        // Очистка корзины
        data object ClearCart : CartEvent
        data object ConfirmClearCart : CartEvent

        // Переход к оформлению заказа
        data object OnProceedOrderClick : CartEvent
    }

    sealed interface CartEffect : BaseContract.BaseEffect {
        data object ProceedOrder : CartEffect
        data class ShowSnackbar(
            val message: StringResource,
            val showToCartButton: Boolean = false,
        ) :
            CartEffect

        data object ShowClearCartConfirmDialog : CartEffect

    }

    data class CartState(
        val isLoading: Boolean = true,
        val proceedOrderIsLoading: Boolean = false,
        val error: UiError? = null,
        val cartItems: List<CartItem> = emptyList(),
        val favoritesItems: Set<CustomizedMeal> = emptySet(),
        val recommends: Recommends = Recommends(),
        val recommendsAreLoading: Boolean = true,
        val pendingDeletionItems: List<String> = emptyList(),
        val inProgressItems: Set<String> = emptySet(),
        val mealDeletionProgress: Map<String, Float> = emptyMap(),
    ) : BaseContract.BaseState {
        val actualCartItems: List<CartItem>
            get() = cartItems.filter { it.id !in pendingDeletionItems && it.quantity > 0 }

        val totalCartPrice: Int
            get() = actualCartItems
                .filter { !it.customizedMeal.meal.isHidden }
                .sumOf { it.customizedMeal.totalPrice() * it.quantity }
    }
}




