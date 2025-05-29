package com.mandarinkafe.mandarin.features.menu.ui.screen

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartViewModel
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuViewModel
import com.mandarinkafe.mandarin.navigation.navigateToSearchScreen
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedViewModel
import com.mandarinkafe.mandarin.util.ui.components.LoadingScreen
import com.mandarinkafe.mandarin.util.ui.screen.PlaceholderScreen

@Composable
fun MenuScreen(
    menuViewModel: MenuViewModel = hiltViewModel(),
    cartViewModel: CartViewModel,
    sharedViewModel: SharedViewModel,
    navController: NavHostController
) {
    val menuSate by menuViewModel.state.collectAsState()
    val cartState by cartViewModel.state.collectAsState()
    val effectFlow = menuViewModel.effect
    val listState = rememberLazyListState()

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
            onSharedEvent = sharedViewModel::onEvent,
            menuSate = menuSate,
            cartState = cartState,
        )
    }

    // Отлавливаем эффект перехода на поиск
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            if (effect is MenuContract.MenuEffect.OpenSearch
            ) {
                navController.navigateToSearchScreen(effect.focusSearch)
            }
        }
    }
}
