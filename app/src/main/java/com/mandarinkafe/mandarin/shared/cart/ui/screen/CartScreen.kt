package com.mandarinkafe.mandarin.shared.cart.ui.screen

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
import com.mandarinkafe.mandarin.core.ui.models.UiError
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.shared.cart.ui.components.CartClearingConfirmationDialog
import com.mandarinkafe.mandarin.shared.cart.ui.components.CartContentScreen
import com.mandarinkafe.mandarin.shared.cart.ui.view_model.CartContract.CartEffect
import com.mandarinkafe.mandarin.shared.cart.ui.view_model.CartContract.CartEvent
import com.mandarinkafe.mandarin.shared.cart.ui.view_model.CartViewModel
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedViewModel
import com.mandarinkafe.mandarin.util.ui.components.LoadingScreen
import com.mandarinkafe.mandarin.util.ui.screen.PlaceholderScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    sharedViewModel: SharedViewModel,
) {
    val listState = rememberLazyListState()
    val state by cartViewModel.state.collectAsState()
    val effectFlow = cartViewModel.effect
    var showClearCartDialog by remember { mutableStateOf(false) }
    val onSharedEvent = sharedViewModel::onEvent
    val onCartEvent = cartViewModel::onEvent
    val favorites by sharedViewModel.favoritesItemsFlow.collectAsState()

    LaunchedEffect(Unit) {
        onCartEvent(CartEvent.Init)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
            .padding(horizontal = Dimens.MarginSmall8)

    ) {
        val error = state.error
        when {
            state.isLoading -> LoadingScreen()
            error != null -> PlaceholderScreen(error = error)
            state.cartItems.isEmpty() -> PlaceholderScreen(UiError.CartEmpty)
            else -> {
                CartContentScreen(
                    listState = listState,
                    state = state,
                    favorites = favorites,
                    onClearCart = { onCartEvent(CartEvent.ClearCart) },
                    onAddToCart = { item -> onCartEvent(CartEvent.AddToCart(item)) },
                    onRemoveFromCart = { item -> onCartEvent(CartEvent.RemoveFromCartWithDelay(item)) },
                    onDeletionCancel = { item -> onCartEvent(CartEvent.CancelRemove(item)) },
                    onToggleFavorite = { item -> onSharedEvent(SharedEvent.ToggleFavorite(item = item)) },
                    onMealDetailsClick = { item -> onSharedEvent(SharedEvent.OnMealDetailsClick(item = item)) },
                    onEditMealClick = { item -> onSharedEvent(SharedEvent.OnEditMealClick(item = item)) },
                )
            }
        }

        // Диалог для подтверждения желания очяистить корзину
        if (showClearCartDialog) {
            CartClearingConfirmationDialog(
                onConfirm = {
                    showClearCartDialog = false
                    cartViewModel.onEvent(CartEvent.ConfirmClearCart)
                },
                onDismiss = {
                    showClearCartDialog = false
                }
            )
        }

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
    }
}
