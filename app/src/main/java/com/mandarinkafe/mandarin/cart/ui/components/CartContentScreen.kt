package com.mandarinkafe.mandarin.cart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
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
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    // Содержимое корзины
                    CartItemsList(
                        cartItems = state.cartItems.entries.map { it.toPair() },
                        listState = listState,
                        modifier = Modifier.padding(bottom = Dimens.MarginStandard16),
                        onEvent = onEvent,
                        pendingDeletionItems = state.pendingDeletionMeals,
                        deletionProgress = state.mealDeletionProgress
                    )

                    Text(
                        modifier = Modifier.padding(Dimens.MarginSmall8),
                        text = stringResource(R.string.question_add_to_cart),
                        style = Typography.TitleStyle
                    )

                    // Рекомендованые товары
                    CartRecommendsList(
                        recommendsList = state.recommendsList,
                        listState = listState,
                        modifier = Modifier,
                        onEvent = onEvent,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    val ifCartIsEmpty =
                        state.cartItems.keys.all { it in state.pendingDeletionMeals }
                    // Кнопка оформления заказа
                    if (!ifCartIsEmpty) {
                        ProcessOrderButton(
                            onClick = { },
                            totalPrice = state.totalCartPrice,
                        )
                    }
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


