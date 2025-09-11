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
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.features.favorites.presentation.ui.components.FavoritesContent
import com.mandarinkafe.mandarin.features.favorites.presentation.viewmodel.FavoritesContract.FavoritesEffect
import com.mandarinkafe.mandarin.features.favorites.presentation.viewmodel.FavoritesViewModel
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEffect
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import com.mandarinkafe.mandarin.util.presentation.ui.components.LoadingScreen
import com.mandarinkafe.mandarin.util.presentation.ui.screen.PlaceholderScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
    cartViewModel: CartViewModel,
    sharedViewModel: SharedViewModel,
) {
    val state by favoritesViewModel.state.collectAsState()
    val cartState by cartViewModel.state.collectAsState()
    val effectFlow = favoritesViewModel.effect
    val listState = rememberLazyListState()
    val onSharedEvent = sharedViewModel::onEvent
    val onCartEvent = cartViewModel::onEvent
    val snackbarHostState = LocalSnackbarHostState.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
    ) {
        when {
            state.isLoading -> LoadingScreen()

            state.error != null -> PlaceholderScreen(
                state.error,
                onCallClick = { onSharedEvent(SharedEvent.OnPhoneClick) },
            )

            state.data.isEmpty() -> PlaceholderScreen(
                UiError.FavoritesEmpty,
                onCallClick = { onSharedEvent(SharedEvent.OnPhoneClick) },
            )

            else -> FavoritesContent(
                listState = listState,
                data = state.data,
                cartItems = cartState.cartItems,
                inProgressItems = cartState.inProgressItems,
                onAddToCart = { onCartEvent(CartEvent.AddToCart(customizedMeal = it)) },
                onRemoveFromCart = { onCartEvent(CartEvent.OnReduce(customizedMeal = it)) },
                onMealDetailsClick = {
                    onSharedEvent(
                        SharedEvent.OnMealDetailsClick(
                            item = it,
                            isEditMode = false
                        )
                    )
                },
                onToggleFavorite = { onSharedEvent(SharedEvent.ToggleFavorite(item = it)) }
            )
        }

        LaunchedEffect(Unit) {
            launch {
                effectFlow.collectLatest { effect ->
                    when (effect) {
                        is FavoritesEffect.ShowSnackbar -> {
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
                    if (effect is SharedEffect.ScrollToTop) {
                        listState.scrollToItem(0)
                    }
                }
            }
        }
    }
}