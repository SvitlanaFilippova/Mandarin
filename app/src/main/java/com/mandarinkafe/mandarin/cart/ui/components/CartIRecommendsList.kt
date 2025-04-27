package com.mandarinkafe.mandarin.cart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract.Event
import com.mandarinkafe.mandarin.core.ui.theme.Colors

@Composable
fun CartRecommendsList(
    recommendsList: List<CartItem>,
    listState: LazyListState,
    onEvent: (Event) -> Unit,
    modifier: Modifier,
) {

    LazyRow(
        state = listState,
        modifier = modifier.background(Colors.Transparent),

        ) {
        itemsIndexed(recommendsList) { _, item ->

            CartRecommendsItemCard(
                item = item, onEvent = onEvent
            )

        }
    }
}