package com.mandarinkafe.mandarin.features.cart.presentation.screen

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.cart.presentation.components.CartClearTextButton
import com.mandarinkafe.mandarin.features.cart.presentation.components.CartItemCard
import com.mandarinkafe.mandarin.features.cart.presentation.components.ProcessOrderButton
import com.mandarinkafe.mandarin.features.cart.presentation.components.RecommendsSection
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartState
import com.mandarinkafe.mandarin.util.Constants.ANIMATION_DURATION_FAST
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalLayoutApi::class)
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
    val ifCartIsEmpty =
        state.cartItems.all { it.id in state.pendingDeletionItems }

    val imeInsets = WindowInsets.ime
    val imeHeight = imeInsets.getBottom(LocalDensity.current)
    val imeVisible = imeHeight > 0

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
                if (state.recommends.mainRecommends.isNotEmpty()) {
                    item {
                        Text(
                            modifier = Modifier.padding(
                                horizontal = Dimens.MarginSmall8,
                                vertical = Dimens.MarginStandard16
                            ),
                            text = stringResource(MR.strings.recommendations_title),
                            style = Typography.TitleStyle
                        )
                    }
                }

                // Горизонтальный список рекомендаций
                item {
                    RecommendsSection(
                        mainRecommends = state.recommends.mainRecommends,
                        separateRecommends = state.recommends.separateRecommends,
                        recommendsAreLoading = state.recommendsAreLoading,
                        onAddToCart = onAddToCart,
                        onMealDetailsClick = onMealDetailsClick
                    )
                }

                // Кнопка оформления заказа (отображается тут только если открыта клавиатура, иначе будет закреплена поверх контента)
                if (!ifCartIsEmpty && imeVisible) {
                    item {
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

                // Отступ для кнопки "Оформить заказ"
                if (!ifCartIsEmpty && !imeVisible) {
                    item { Spacer(modifier = Modifier.height(Dimens.MarginForCartButton)) }
                }
            }

            // Кнопка оформления заказа (закреплена поверх контента, скрывается при открытой клавиатуре)
            if (!ifCartIsEmpty && !imeVisible) {
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
