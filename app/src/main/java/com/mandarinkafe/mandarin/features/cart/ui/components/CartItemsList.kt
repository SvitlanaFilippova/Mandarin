package com.mandarinkafe.mandarin.features.cart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.features.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract.CartEvent

@Composable
fun CartItemsList(
    cartItems: List<Pair<CartItem, Int>>,
    pendingDeletionItems: List<CartItem>,
    listState: LazyListState,
    onEvent: (CartEvent) -> Unit,
    modifier: Modifier,
    deletionProgress: Map<CartItem, Float>
) {

    LazyColumn(
        state = listState,
        modifier = modifier.background(Colors.Transparent),
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
        itemsIndexed(cartItems) { _, (cartItem, quantity) ->

            val itemInPendingDeletion = pendingDeletionItems.contains(cartItem)

            CartItemCard(
                item = cartItem,
                quantity = quantity, onEvent = onEvent,
                itemInPendingDeletion = itemInPendingDeletion,
                deletionProgress = deletionProgress[cartItem] ?: 0f
            )
        }
    }
}