package com.mandarinkafe.mandarin.cart.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract.Event

@Composable
fun CartContentScreen(
    listState: LazyListState,
    onEvent: (Event) -> Unit,
    state: CartContract.State
) {

    Column {
        CarClearTextButton(
            onClear = { onEvent(Event.ClearCart) }
        )
        CartItemsList(
            cartItems = state.cartItems,
            listState = listState,
            modifier = Modifier.weight(1f),
            onEvent = onEvent
        )
        ProcessOrderButton(
            onClick = { },
            totalPrice = state.totalCartPrice,
        )

    }
}