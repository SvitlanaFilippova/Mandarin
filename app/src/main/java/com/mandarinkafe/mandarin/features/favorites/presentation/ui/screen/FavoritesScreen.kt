package com.mandarinkafe.mandarin.features.favorites.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.features.favorites.presentation.ui.components.FavoritesContent
import com.mandarinkafe.mandarin.features.favorites.presentation.viewmodel.FavoritesViewModel
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.util.presentation.ui.components.LoadingScreen
import com.mandarinkafe.mandarin.util.presentation.ui.screen.PlaceholderScreen

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
                error,
                onCallClick = { onSharedEvent(SharedEvent.OnPhoneClick) },
            )

            state.data.isEmpty() -> PlaceholderScreen(
                UiError.FavoritesEmpty,
                onCallClick = { onSharedEvent(SharedEvent.OnPhoneClick) },
            )

            else -> FavoritesContent(
                data = state.data,
                cartItems = cartState.cartItems,
                onAddToCart = { onCartEvent(CartEvent.AddToCart(customizedMeal = it)) },
                onRemoveFromCart = { onCartEvent(CartEvent.RemoveFromCartByCustomizedMeal(it)) },
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
    }
}