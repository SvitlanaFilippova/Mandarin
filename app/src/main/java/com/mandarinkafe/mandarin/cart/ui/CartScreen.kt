package com.mandarinkafe.mandarin.cart.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.cart.ui.components.CartItemsList
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens

@Preview
@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
) {
    val listState = rememberLazyListState()
    val state by viewModel.state.collectAsState()
    val cartItems = state.cartItems

    cartItems.sumOf { it.price + it.adds.sumOf { it.price } }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
            .padding(Dimens.MarginSmall8)

    ) {
        CartItemsList(
            cartItems = cartItems,
            listState = listState,
            modifier = Modifier.weight(1f)
        )
    }
}