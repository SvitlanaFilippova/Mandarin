package com.mandarinkafe.mandarin.features.cart.presentation.screen

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.cart.data.CartMapper.toCartItem
import com.mandarinkafe.mandarin.features.cart.presentation.components.CartClearTextButton
import com.mandarinkafe.mandarin.features.cart.presentation.components.CartItemCard
import com.mandarinkafe.mandarin.features.cart.presentation.components.CartRecommendsList
import com.mandarinkafe.mandarin.features.cart.presentation.components.ProcessOrderButton
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartState
import com.mandarinkafe.mandarin.util.Constants.ANIMATION_DURATION_FAST
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyCircularProgressIndicator

@Composable
fun CartContentScreen(
    state: CartState,
    listState: LazyListState,
    favorites: List<CustomizedMeal>,
    proceedOrderIsLoading: Boolean,
    onClearCart: () -> Unit,
    onShowFavoriteDialog: (CustomizedMeal) -> Unit,
    onToggleFavorite: (CustomizedMeal) -> Unit,
    onAddToCart: (CartItem) -> Unit,
    onRemoveFromCart: (CartItem) -> Unit,
    onDeletionCancel: (CartItem) -> Unit,
    onMealDetailsClick: (CartItem) -> Unit,
    onProceedOrderClick: () -> Unit,
    onCommentAdded: (CartItem, String) -> Unit,
) {
    val cartItemsList = state.cartItems

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.MarginSmall8)
    ) {
        // Кнопка очистки корзины,
        CartClearTextButton(
            onClear = onClearCart,
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                state = listState
            ) {
                item {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.MarginSmall8),
                        thickness = Dimens.DividerHeight1,
                        color = Colors.LightGrey.copy(alpha = 0.2f)
                    )
                }

                // Список элементов корзины
                itemsIndexed(
                    items = cartItemsList,
                    key = { _, cartItem -> cartItem.id }
                ) { index, cartItem ->
                    val itemInPendingDeletion = state.pendingDeletionItems.contains(cartItem.id)
                    val isInProgress = cartItem.id in state.inProgressItems
                    CartItemCard(
                        modifier = Modifier.animateItem(tween(ANIMATION_DURATION_FAST)),
                        item = cartItem,
                        favorites = favorites,
                        itemInPendingDeletion = itemInPendingDeletion,
                        deletionProgress = state.mealDeletionProgress[cartItem.id] ?: 0f,
                        isInProgress = isInProgress,
                        onToggleFavorite = onToggleFavorite,
                        onShowFavoriteDialog = onShowFavoriteDialog,
                        onAddToCart = { onAddToCart(cartItem) },
                        onRemoveFromCart = { onRemoveFromCart(cartItem) },
                        onDeletionCancel = { onDeletionCancel(cartItem) },
                        onMealDetailsClick = { onMealDetailsClick(cartItem) },
                        onCommentAdded = onCommentAdded,
                    )
                }

                // Заголовок рекомендаций
                if (state.recommends.isNotEmpty()) {
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
                }

                // Горизонтальный список рекомендаций
                item {
                    if (state.recommendsAreLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Dimens.MarginStandard16),
                            contentAlignment = Alignment.Center
                        ) {
                            MyCircularProgressIndicator(
                                strokeWidth = Dimens.ProgressBarStroke6,
                            )
                        }
                    } else {
                        CartRecommendsList(
                            recommendsList = state.recommends,
                            modifier = Modifier.padding(bottom = Dimens.MarginStandard16),
                            onAddToCart = { onAddToCart(it.toCartItem()) },
                            onMealDetailsClick = { onMealDetailsClick(it.toCartItem()) },
                        )
                    }
                }

                // Отступ для кнопки "Оформить заказ"
                item { Spacer(modifier = Modifier.height(Dimens.MarginForCartButton72)) }
            }

            // Кнопка оформления заказа
            val ifCartIsEmpty =
                state.cartItems.all { it.id in state.pendingDeletionItems }
            if (!ifCartIsEmpty) {
                ProcessOrderButton(
                    onClick = onProceedOrderClick,
                    totalPrice = state.totalCartPrice,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .background(color = Colors.Transparent),
                    proceedOrderIsLoading = proceedOrderIsLoading
                )
            }
        }
    }
}
