package com.mandarinkafe.mandarin.features.cart.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEffect
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.navigation.extensions.navigateToOrder
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.util.presentation.ui.components.ConfirmationDialog
import com.mandarinkafe.mandarin.util.presentation.ui.components.LoadingScreen
import com.mandarinkafe.mandarin.util.presentation.ui.screen.PlaceholderScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    sharedViewModel: SharedViewModel,
    navController: NavHostController
) {
    val listState = rememberLazyListState()
    val state by cartViewModel.state.collectAsState()
    val effectFlow = cartViewModel.effect
    val onSharedEvent = sharedViewModel::onEvent
    val onCartEvent = cartViewModel::onEvent
    val favorites by sharedViewModel.favoritesItemsFlow.collectAsState()
    var showClearCartDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onCartEvent(CartEvent.Init)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
    ) {
        val error = state.error
        when {
            state.isLoading -> LoadingScreen()
            error != null -> PlaceholderScreen(
                error = error,
                onCallClick = { onSharedEvent(SharedEvent.OnPhoneClick) },
            )

            state.cartItems.isEmpty() -> PlaceholderScreen(
                UiError.CartEmpty,
                onCallClick = { onSharedEvent(SharedEvent.OnPhoneClick) },
            )

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
                    onShowFavoriteDialog = { item ->
                        onSharedEvent(
                            SharedEvent.ShowFavoriteDialog(
                                item = item
                            )
                        )
                    },
                    onMealDetailsClick = { item -> onSharedEvent(SharedEvent.OnMealDetailsClick(item = item)) },
                    onEditMealClick = { item -> onSharedEvent(SharedEvent.OnEditMealClick(item = item)) },
                    onProceedOrderClick = { onCartEvent(CartEvent.OnProceedOrderClick) },
                )
            }
        }

        // Диалог для подтверждения желания очяистить корзину
        if (showClearCartDialog) {
            ConfirmationDialog(
                titleRes = R.string.clear_cart_question,
                textRes = R.string.clear_cart_confirmation,
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
                    is CartEffect.ShowClearCartConfirmDialog -> {
                        showClearCartDialog = true
                    }

                    is CartEffect.ProceedOrder -> {
                        navController.navigateToOrder()
                    }

                    else -> {}
                }
            }
        }
    }
}


