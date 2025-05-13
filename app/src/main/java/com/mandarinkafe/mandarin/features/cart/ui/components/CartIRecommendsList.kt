package com.mandarinkafe.mandarin.features.cart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.features.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract.Event

@Composable
fun CartRecommendsList(
    recommendsList: List<CartItem>,
    onEvent: (Event) -> Unit,
    modifier: Modifier,
) {

    LazyRow(
        modifier = modifier.background(Colors.Transparent),
        ) {
        itemsIndexed(recommendsList) { _, item ->

            CartRecommendsItemCard(
                item = item, onEvent = onEvent
            )

        }
    }
}