package com.mandarinkafe.mandarin.features.favorites.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.features.favorites.presentation.ui.components.FavoritesContent
import com.mandarinkafe.mandarin.features.favorites.presentation.viewmodel.FavoritesContract
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberFavoritesViewModel
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import com.mandarinkafe.mandarin.util.presentation.ui.screen.PlaceholderScreen
import dev.materii.pullrefresh.PullRefreshIndicator
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    cartViewModel: CartViewModel,
    sharedViewModel: SharedViewModel,
) {
    val favoritesViewModel = rememberFavoritesViewModel()
    val state by favoritesViewModel.state.collectAsState()
    val cartState by cartViewModel.state.collectAsState()
    val effectFlow = favoritesViewModel.effect
    val listState = rememberLazyListState()
    val onEvent = favoritesViewModel::onEvent
    val onSharedEvent = sharedViewModel::onEvent
    val onCartEvent = cartViewModel::onEvent
    val snackbarHostState = LocalSnackbarHostState.current

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = { onEvent(FavoritesContract.FavoritesEvent.ForceRefresh) }
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
            when {
                state.error != null -> PlaceholderScreen(
                    state.error,
                    onCallClick = { onSharedEvent(SharedContract.SharedEvent.OnPhoneClick()) },
                    onRetryClick = { onEvent(FavoritesContract.FavoritesEvent.ForceRefresh) },
                )

                state.data.isEmpty() && !state.isLoading -> PlaceholderScreen(
                    UiError.FavoritesEmpty,
                    onCallClick = { onSharedEvent(SharedContract.SharedEvent.OnPhoneClick()) },
                )

                else -> FavoritesContent(
                    listState = listState,
                    data = state.data,
                    cartItems = cartState.cartItems,
                    inProgressItems = cartState.inProgressItems,
                    onAddToCart = { onCartEvent(CartContract.CartEvent.AddToCart(customizedMeal = it)) },
                    onRemoveFromCart = { onCartEvent(CartContract.CartEvent.OnReduce(customizedMeal = it)) },
                    onMealDetailsClick = {
                        onSharedEvent(
                            SharedContract.SharedEvent.OnMealDetailsClick(
                                item = it,
                                isEditMode = false
                            )
                        )
                    },
                    onToggleFavorite = {
                        onSharedEvent(
                            SharedContract.SharedEvent.ToggleFavorite(
                                item = it
                            )
                        )
                    }
                )
            }

        }

    }

    LaunchedEffect(Unit) {
        launch {
            effectFlow.collectLatest { effect ->
                when (effect) {
                    is FavoritesContract.FavoritesEffect.ShowSnackbar -> {
                        snackbarHostState.showSnackbar(
                            message = effect.message,
                            duration = SnackbarDuration.Long,
                            withDismissAction = true,
                        )
                    }
                }
            }
        }
        launch {
            sharedViewModel.effect.collect { effect ->
                if (effect is SharedContract.SharedEffect.ScrollToTop) {
                    listState.scrollToItem(0)
                }
            }
        }
    }
}
