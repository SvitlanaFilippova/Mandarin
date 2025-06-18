package com.mandarinkafe.mandarin.features.cart.ui.screen

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.cart.ui.components.CartClearTextButton
import com.mandarinkafe.mandarin.features.cart.ui.components.CartItemCard
import com.mandarinkafe.mandarin.features.cart.ui.components.CartRecommendsList
import com.mandarinkafe.mandarin.features.cart.ui.components.ProcessOrderButton
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract.CartState
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.MyCircularProgressIndicator

@Composable
fun CartContentScreen(
    state: CartState,
    listState: LazyListState,
    favorites: List<CustomizedMeal>,
    onClearCart: () -> Unit,
    onShowFavoriteDialog: (CustomizedMeal) -> Unit,
    onToggleFavorite: (CustomizedMeal) -> Unit,
    onAddToCart: (CustomizedMeal) -> Unit,
    onRemoveFromCart: (CustomizedMeal) -> Unit,
    onDeletionCancel: (CustomizedMeal) -> Unit,
    onMealDetailsClick: (CustomizedMeal) -> Unit,
    onEditMealClick: (CustomizedMeal) -> Unit,
) {

    val cartItemsList: List<Pair<CustomizedMeal, Int>> = remember(state.cartItems) {
        state.cartItems.entries.map { it.toPair() }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Кнопка очистки корзины,
        CartClearTextButton(
            onClear = onClearCart,
        )

        Box(
            modifier = Modifier.fillMaxSize()
        )
        {
            LazyColumn(
                state = listState
            ) {
                // Список элементов корзины
                itemsIndexed(
                    items = cartItemsList,
                    key = { _, pair -> pair.first.id }
                ) { index, pair ->
                    val cartItem = pair.first
                    val itemInPendingDeletion =
                        state.pendingDeletionMeals.contains(cartItem)

                    CartItemCard(
                        modifier = Modifier.animateItem(tween(Constants.ANIMATION_DURATION_FAST)),
                        item = cartItem,
                        quantity = pair.second,
                        favorites = favorites,
                        itemInPendingDeletion = itemInPendingDeletion,
                        deletionProgress = state.mealDeletionProgress[cartItem] ?: 0f,
                        onToggleFavorite = onToggleFavorite,
                        onShowFavoriteDialog = onShowFavoriteDialog,
                        onAddToCart = onAddToCart,
                        onRemoveFromCart = onRemoveFromCart,
                        onDeletionCancel = onDeletionCancel,
                        onMealDetailsClick = onMealDetailsClick,
                        onEditMealClick = onEditMealClick,
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
                        MyCircularProgressIndicator(
                            strokeWidth = Dimens.ProgressBarSmallWidth8,
                        )
                    } else {

                        CartRecommendsList(
                            recommendsList = state.recommends,
                            modifier = Modifier.padding(bottom = Dimens.MarginStandard16),
                            onAddToCart = onAddToCart,
                            onMealDetailsClick = onMealDetailsClick,
                        )

                    }
                }

                // Отступ для кнопки "Оформить заказ"
                item { Spacer(modifier = Modifier.height(Dimens.MarginForCartButton72)) }
            }

            // Кнопка оформления заказа
            val ifCartIsEmpty =
                state.cartItems.keys.all { it in state.pendingDeletionMeals }
            if (!ifCartIsEmpty) {
                ProcessOrderButton(
                    onClick = { /* обработка нажатия */ },
                    totalPrice = state.totalCartPrice,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .background(color = Colors.Transparent)
                )
            }
        }
    }
}
