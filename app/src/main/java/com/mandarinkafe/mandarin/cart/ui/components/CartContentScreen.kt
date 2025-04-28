package com.mandarinkafe.mandarin.cart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract.Event
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography

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
                modifier = Modifier.weight(1f)
            )
            {

                LazyColumn(
                    state = listState
                ) {
                    // Список элементов корзины
                    itemsIndexed(state.cartItems.entries.map { it.toPair() }) { index, (cartItem, quantity) ->
                        val itemInPendingDeletion = state.pendingDeletionMeals.contains(cartItem)

                        CartItemCard(
                            item = cartItem,
                            quantity = quantity,
                            onEvent = onEvent,
                            itemInPendingDeletion = itemInPendingDeletion,
                            deletionProgress = state.mealDeletionProgress[cartItem] ?: 0f,
                        )
                    }

                    // Заголовок рекомендаций
                    item {
                        Text(
                            modifier = Modifier.padding(
                                horizontal = Dimens.MarginSmall8,
                                vertical = Dimens.MarginStandard16
                            ),
                            text = stringResource(R.string.question_add_to_cart),
                            style = Typography.TitleStyle
                        )
                    }

                    // Горизонтальный список рекомендаций
                    item {
                        CartRecommendsList(
                            recommendsList = state.recommendsList,
                            modifier = Modifier.padding(bottom = Dimens.MarginStandard16),
                            onEvent = onEvent,
                        )
                    }
                }

                // Затемнение поверх LazyColumn содержимого
                if (isPendingClear) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Colors.AppBackgroundColor.copy(alpha = 0.7f))
                            .clickable(enabled = false) {} // блокирует клики по списку
                    )
                }
            }

            // Кнопка оформления заказа
            val ifCartIsEmpty =
                state.cartItems.keys.all { it in state.pendingDeletionMeals }
            if (!ifCartIsEmpty && !isPendingClear) {
                ProcessOrderButton(
                    onClick = { /* обработка нажатия */ },
                    totalPrice = state.totalCartPrice,
                    modifier = Modifier.padding(vertical = Dimens.MarginSmall8)
                )
            }
        }
    }
}
