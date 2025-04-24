package com.mandarinkafe.mandarin.cart.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.cart.ui.components.CartItemsList
import com.mandarinkafe.mandarin.cart.ui.components.CartTopBar
import com.mandarinkafe.mandarin.cart.ui.components.ProcessOrderButton
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.cart.ui.view_model.CartViewModel
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography

@Preview
@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
) {
    val listState = rememberLazyListState()
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
            .padding(Dimens.MarginSmall8)

    ) {
        CartTopBar()
        ClearCartRow(
            onClear = { viewModel.onEvent(CartContract.Event.ClearCart) }
        )
        CartItemsList(
            cartItems = state.cartItems,
            listState = listState,
            modifier = Modifier.weight(1f),
            onEvent = viewModel::onEvent
        )
        ProcessOrderButton(
            onClick = { },
            totalPrice = state.totalCartPrice,
        )
    }
}

@Composable
fun ClearCartRow(onClear: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable { onClear },
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Text(
            text = stringResource(R.string.clear_cart),
            style = Typography.SmallTextStyle,
            color = Colors.Grey
        )

        Icon(
            modifier = Modifier
                .size(Dimens.IconSize24),
            imageVector = Icons.Default.Delete,
            tint = Colors.Grey,
            contentDescription = stringResource(R.string.clear_cart),

            )
    }
}