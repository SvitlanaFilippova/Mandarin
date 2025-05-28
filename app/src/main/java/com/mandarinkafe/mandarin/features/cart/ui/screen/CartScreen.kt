package com.mandarinkafe.mandarin.features.cart.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.features.cart.ui.components.CartClearingConfirmationDialog
import com.mandarinkafe.mandarin.features.cart.ui.components.CartContentScreen
import com.mandarinkafe.mandarin.features.cart.ui.components.CartTopBar
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract.CartEffect
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract.CartEffect.OpenMealDetailsBS
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract.CartEvent
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartViewModel
import com.mandarinkafe.mandarin.features.meal_details.ui.screen.MealDetailsBottomSheet
import com.mandarinkafe.mandarin.util.ui.HandleBottomSheetEffect
import com.mandarinkafe.mandarin.util.ui.components.LoadingScreen
import com.mandarinkafe.mandarin.util.ui.screen.PlaceholderScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CartScreen(
    viewModel: CartViewModel
) {
    val listState = rememberLazyListState()
    val state by viewModel.state.collectAsState()
    val effectFlow = viewModel.effect
    var showClearCartDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is CartEffect.ShowClearCartConfirmationDialog -> {
                    showClearCartDialog = true
                }

                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
            .padding(horizontal = Dimens.MarginSmall8)

    ) {

        CartTopBar(
            onCallClick = { }
        )
        val error = state.error
        when {
            state.isLoading -> LoadingScreen()
            error != null -> PlaceholderScreen(error = error)
            state.cartItems.isNotEmpty() -> {
                CartContentScreen(
                    listState = listState,
                    onEvent = viewModel::onEvent,
                    state = state
                )

            }
        }

        if (showClearCartDialog) {
            CartClearingConfirmationDialog(
                onConfirm = {
                    showClearCartDialog = false
                    viewModel.onEvent(CartEvent.ConfirmClearCart)
                },
                onDismiss = {
                    showClearCartDialog = false
                }
            )
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
                        CartEvent.ReplaceMealInCart(
                            newItem = newItem,
                            oldItem = effect.item
                        )
                    )
                }
            )
        }
    }
}
