package com.mandarinkafe.mandarin.cart.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract.Event
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.Meal

@Composable
fun CartItemsList(
    cartItems: List<CartItem>,
    pendingDeletionItems: List<Meal>,
    listState: LazyListState,
    onEvent: (Event) -> Unit,
    modifier: Modifier,
) {

    LazyColumn(
        state = listState,
        modifier = modifier.background(Colors.Transparent),
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
        itemsIndexed(cartItems) { _, item ->
            val mealInPendingDeletion = pendingDeletionItems.contains(item.meal)

            //при удалении карточка исчезает с задержкой и при отмене возвращается
            AnimatedVisibility(
                visible = !mealInPendingDeletion,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CartItemCard(
                    item = item, onEvent = onEvent,
                    mealInPendingDeletion = mealInPendingDeletion
                )
            }
        }
    }
}