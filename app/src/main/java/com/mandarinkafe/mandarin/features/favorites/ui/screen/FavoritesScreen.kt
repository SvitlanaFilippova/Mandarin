package com.mandarinkafe.mandarin.features.favorites.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartViewModel
import com.mandarinkafe.mandarin.features.favorites.ui.components.FavoritesContent
import com.mandarinkafe.mandarin.features.favorites.ui.view_model.FavoritesViewModel
import com.mandarinkafe.mandarin.features.menu.ui.components.MenuTopBar
import com.mandarinkafe.mandarin.shared.placeholder.ui.screen.PlaceholderScreen
import com.mandarinkafe.mandarin.util.ui.components.LoadingScreen

@Composable
fun FavoritesScreen(
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
    cartViewModel: CartViewModel
) {
    val state by favoritesViewModel.state.collectAsState()
    val cartState by cartViewModel.state.collectAsState()
    val effectFlow = favoritesViewModel.effect

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
    ) {
        MenuTopBar(
            onPhoneClick = { }, //TODO
            onLogoCLick = { return@MenuTopBar }
        )

        when {
            state.isLoading -> LoadingScreen()
            state.errorMessage != null -> PlaceholderScreen(
                state.errorMessage!!,
            )

            else -> FavoritesContent(
                data = state.data,
                onEvent = favoritesViewModel::onEvent,
                onCartEvent = cartViewModel::onEvent,
                cartState = cartState,
                effectFlow = effectFlow
            )

        }
    }
}