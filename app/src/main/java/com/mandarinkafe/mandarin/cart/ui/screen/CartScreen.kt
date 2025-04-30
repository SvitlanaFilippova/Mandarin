package com.mandarinkafe.mandarin.cart.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.cart.ui.components.CartContentScreen
import com.mandarinkafe.mandarin.cart.ui.components.CartPlaceholder
import com.mandarinkafe.mandarin.cart.ui.components.CartTopBar
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract.Effect.OpenMealDetailsBS
import com.mandarinkafe.mandarin.cart.ui.view_model.CartViewModel
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.meal_details.ui.screen.MealDetailsBottomSheet
import com.mandarinkafe.mandarin.util.ui.HandleBottomSheetEffect
import com.mandarinkafe.mandarin.util.ui.components.LoadingScreen

@Preview
@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
) {
    val listState = rememberLazyListState()
    val state by viewModel.state.collectAsState()
    val effectFlow = viewModel.effect

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
            .padding(horizontal = Dimens.MarginSmall8)

    ) {

        CartTopBar(
            onBackClick = { },
            onCallClick = { }
        )

        if (state.isLoading) {
            LoadingScreen()
            return
        }
        if (state.cartItems.isNotEmpty()) {
            CartContentScreen(
                listState = listState,
                onEvent = viewModel::onEvent,
                state = state
            )
        } else {
            CartPlaceholder(
                stringResource(R.string.error_empty_cart)
            )
        }
    }

    HandleBottomSheetEffect<OpenMealDetailsBS>(
        effectFlow = effectFlow,
        cast = { it as? OpenMealDetailsBS }
    ) { effect, onDismiss ->
        MealDetailsBottomSheet(
            initItem = effect.item,
            onDismiss = onDismiss,
            onAddToCart = { newItem ->
                viewModel.onEvent(
                    CartContract.Event.ReplaceMealInCart(
                        newItem = newItem,
                        oldItem = effect.item
                    )
                )
            }
        )
    }
}
