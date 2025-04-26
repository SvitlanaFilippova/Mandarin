package com.mandarinkafe.mandarin.cart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract.Event
import com.mandarinkafe.mandarin.core.ui.theme.Colors

@Composable
fun CartContentScreen(
    listState: LazyListState,
    onEvent: (Event) -> Unit,
    state: CartContract.State
) {
    val isPendingClear = state.isPendingDeletion

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            // Кнопка очистки корзины,
            CartClearTextButton(
                onClear = { onEvent(Event.ClearCart) },
                onCancelClear = { onEvent(Event.CancelClearingCart) },
                isPendingClear = isPendingClear,
                clearingProgress = state.cartClearingProgress
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {

                CartItemsList(
                    cartItems = state.cartItems,
                    listState = listState,
                    modifier = Modifier.fillMaxSize(),
                    onEvent = onEvent,
                    pendingDeletionItems = state.pendingDeletionMeals,
                    deletionProgress = state.mealDeletionProgress
                )
                val ifCartIsEmpty = state.cartItems.none { it.meal !in state.pendingDeletionMeals }

                // Кнопка оформления заказа
                if (!ifCartIsEmpty) {
                ProcessOrderButton(
                    onClick = { },
                    totalPrice = state.totalCartPrice,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
                }

                // Затемняем и делаем неактивными CartItemsList и ProcessOrderButton, если корзина в процессе удаления
                if (isPendingClear) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(enabled = false, onClick = { })
                            .background(Colors.AppBackgroundColor.copy(alpha = 0.7f))

                    )
                }
            }
        }
    }
}