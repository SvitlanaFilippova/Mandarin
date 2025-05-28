package com.mandarinkafe.mandarin.features.menu.ui.screen

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartViewModel
import com.mandarinkafe.mandarin.features.menu.ui.components.HandleEffects
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuViewModel
import com.mandarinkafe.mandarin.util.ui.components.LoadingScreen
import com.mandarinkafe.mandarin.util.ui.screen.PlaceholderScreen

@Composable
fun MenuScreen(
    menuViewModel: MenuViewModel = hiltViewModel(),
    cartViewModel: CartViewModel,
    navController: NavHostController
) {

    val menuSate by menuViewModel.state.collectAsState()
    val cartState by cartViewModel.state.collectAsState()
    val effectFlow = menuViewModel.effect
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val error = menuSate.error

    when {
        menuSate.isLoading -> LoadingScreen()
        error != null -> PlaceholderScreen(
            error = error,
        )

        else -> MenuContentScreen(
            listState = listState,
            onEvent = menuViewModel::onEvent,
            onCartEvent = cartViewModel::onEvent,
            menuSate = menuSate,
            cartState = cartState,
            effectFlow = effectFlow
        )
    }
    HandleEffects(
        effectFlow = effectFlow,
        navController = navController,
        context = context
    )
}
