package com.mandarinkafe.mandarin.features.cart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract.CartEvent
import com.mandarinkafe.mandarin.util.Constants.BLUR_EFFECT_RADIUS

@Composable
fun CartContentScreen(
    listState: LazyListState,
    onEvent: (CartEvent) -> Unit,
    state: CartContract.CartState
) {
    val isPendingClear = state.isPendingDeletion
    var modifierForLazyColumnBox =
        if (isPendingClear) {
            Modifier.graphicsLayer {
                renderEffect = BlurEffect(
                    radiusX = BLUR_EFFECT_RADIUS,
                    radiusY = BLUR_EFFECT_RADIUS,
                )
            }
        } else {
            Modifier
        }

    Column(modifier = Modifier.fillMaxSize()) {

        // Кнопка очистки корзины,
        CartClearTextButton(
            onClear = { onEvent(CartEvent.ClearCart) },
            onCancelClear = { onEvent(CartEvent.CancelClearingCart) },
            isPendingClear = isPendingClear,
            clearingProgress = state.cartClearingProgress
        )

        Box(
            modifier = modifierForLazyColumnBox.fillMaxSize()
        )
        {
            LazyColumn(
                state = listState
            ) {
                // Список элементов корзины
                itemsIndexed(state.cartItems.entries.map { it.toPair() }) { index, (cartItem, quantity) ->
                    val itemInPendingDeletion =
                        state.pendingDeletionMeals.contains(cartItem)

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

                // Отступ для кнопки "Оформить заказ"
                item { Spacer(modifier = Modifier.height(Dimens.MarginForCartButton72)) }
            }

            // Затемнение поверх LazyColumn содержимого

            // Кнопка оформления заказа
            val ifCartIsEmpty =
                state.cartItems.keys.all { it in state.pendingDeletionMeals }
            if (!ifCartIsEmpty) {
                ProcessOrderButton(
                    onClick = { /* обработка нажатия */ },
                    totalPrice = state.totalCartPrice,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(vertical = Dimens.MarginSmall8)
                        .background(color = Colors.Transparent)
                )
            }

            if (isPendingClear) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Colors.AppBackgroundColor.copy(alpha = 0.7f))
                        .clickable(enabled = false) {}
                )
            }
        }
    }
}
