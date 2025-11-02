package com.mandarinkafe.mandarin.features.cart.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEffect
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.navigation.extensions.navigateToOrder
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEffect
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import com.mandarinkafe.mandarin.util.presentation.ui.components.dialogs.RemoveConfirmationDialog
import com.mandarinkafe.mandarin.util.presentation.ui.screen.PlaceholderScreen
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.materii.pullrefresh.PullRefreshIndicator
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState
import kotlinx.coroutines.launch


@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    sharedViewModel: SharedViewModel,
    navController: NavController,
    snackbarMessage: String? = null,
) {
    val listState = rememberLazyListState()
    val state by cartViewModel.state.collectAsState()
    val effectFlow = cartViewModel.effect
    val onSharedEvent = sharedViewModel::onEvent
    val onCartEvent = cartViewModel::onEvent
    val favorites by sharedViewModel.favoritesItemsFlow.collectAsState()
    var showClearCartDialog by remember { mutableStateOf(false) }
    val snackbarHostState = LocalSnackbarHostState.current
    var pendingSnackbarRes: StringResource? by remember { mutableStateOf(null) }
    var pendingShowToCart: Boolean by remember { mutableStateOf(false) }

    snackbarMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(
                message = message,
                withDismissAction = true,
            )
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = { onCartEvent(CartEvent.ForceRefresh) }
    )

    PullRefreshLayout(
        modifier = Modifier.fillMaxSize(),
        state = pullRefreshState,
        indicator = {
            PullRefreshIndicator(
                state = pullRefreshState,
                contentColor = Colors.Orange,
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.AppBlack)
        ) {
            val error = state.error
            when {
                error != null -> PlaceholderScreen(
                    error = error,
                    onCallClick = { onSharedEvent(SharedEvent.OnPhoneClick()) },
                    onRetryClick = { onCartEvent(CartEvent.ForceRefresh) }
                )

                state.cartItems.isEmpty() && !state.isLoading -> {
                    PlaceholderScreen(
                        UiError.CartEmpty,
                        onCallClick = { onSharedEvent(SharedEvent.OnPhoneClick()) },
                    )
                }

                else -> {
                    CartContentScreen(
                        listState = listState,
                        state = state,
                        favorites = favorites,
                        proceedOrderIsLoading = state.proceedOrderIsLoading,
                        onClearCart = { onCartEvent(CartEvent.ClearCart) },
                        onAddToCart = { item -> onCartEvent(CartEvent.AddToCart(item)) },
                        onRemoveFromCart = { item -> onCartEvent(CartEvent.OnReduceWithDelay(item)) },
                        onDeletionCancel = { item -> onCartEvent(CartEvent.CancelRemove(item)) },
                        onToggleFavorite = { item -> onSharedEvent(SharedEvent.ToggleFavorite(item = item)) },
                        onShowFavoriteDialog = { item ->
                            onSharedEvent(
                                SharedEvent.ShowFavoriteDialog(
                                    item = item
                                )
                            )
                        },
                        onMealDetailsClick = { item ->
                            onSharedEvent(
                                SharedEvent.OnMealDetailsClick(
                                    cartItem = item,
                                    isEditMode = true
                                )
                            )
                        },
                        onProceedOrderClick = { onCartEvent(CartEvent.OnProceedOrderClick) },
                        onCommentAdded = { item, text ->
                            onCartEvent(
                                CartEvent.AddCommentToItem(
                                    item,
                                    text
                                )
                            )
                        },
                    )
                }
            }
        }
    }

    // Диалог для подтверждения желания очистить корзину
    if (showClearCartDialog) {
        RemoveConfirmationDialog(
            title = stringResource(MR.strings.clear_cart_question),
            text = stringResource(MR.strings.clear_cart_confirmation),
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
        launch {
            effectFlow.collect { effect ->
                when (effect) {
                    is CartEffect.ShowClearCartConfirmDialog -> showClearCartDialog = true
                    is CartEffect.ProceedOrder -> navController.navigateToOrder()
                    is CartEffect.ShowSnackbar -> {
                        pendingSnackbarRes = effect.message
                        pendingShowToCart = effect.showToCartButton
                    }
                }
            }
        }
        launch {
            sharedViewModel.effect.collect { effect ->
                if (effect is SharedEffect.ScrollToTop) {
                    listState.scrollToItem(0)
                }
            }
        }
    }

    val pendingMessage: String? = pendingSnackbarRes?.let { stringResource(it) }

    LaunchedEffect(pendingMessage, pendingShowToCart) {
        val message = pendingMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(
            message = message,
            duration = SnackbarDuration.Long,
            withDismissAction = true,
        )
        pendingSnackbarRes = null
        pendingShowToCart = false
    }
}
