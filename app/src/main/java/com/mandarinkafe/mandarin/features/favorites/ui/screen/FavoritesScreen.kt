package com.mandarinkafe.mandarin.features.favorites.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.core.ui.models.UiError
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.features.favorites.ui.components.FavoritesContent
import com.mandarinkafe.mandarin.features.favorites.ui.view_model.FavoritesViewModel
import com.mandarinkafe.mandarin.shared.cart.ui.view_model.CartContract.CartEvent
import com.mandarinkafe.mandarin.shared.cart.ui.view_model.CartViewModel
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedViewModel
import com.mandarinkafe.mandarin.util.ui.components.LoadingScreen
import com.mandarinkafe.mandarin.util.ui.screen.PlaceholderScreen

@Composable
fun FavoritesScreen(
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
    cartViewModel: CartViewModel,
    sharedViewModel: SharedViewModel,
) {
    val state by favoritesViewModel.state.collectAsState()
    val cartState by cartViewModel.state.collectAsState()

    val onSharedEvent = sharedViewModel::onEvent
    val onCartEvent = cartViewModel::onEvent

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
    ) {

        val error = state.error

        when {
            state.isLoading -> LoadingScreen()
            error != null -> PlaceholderScreen(
                error
            )

            state.data.isEmpty() -> PlaceholderScreen(UiError.FavoritesEmpty)

            else -> FavoritesContent(
                data = state.data,
                cartState = cartState,
                onAddToCart = { item -> onCartEvent(CartEvent.AddToCart(item)) },
                onRemoveFromCart = { item -> onCartEvent(CartEvent.RemoveFromCartByItem(item)) },
                onMealDetailsClick = { item -> onSharedEvent(SharedEvent.OnMealDetailsClick(item = item)) },
                onToggleFavorite = { item -> onSharedEvent(SharedEvent.ToggleFavorite(item = item)) }
            )
        }
    }
}