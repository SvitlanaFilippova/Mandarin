package com.mandarinkafe.mandarin.features.menu.presentation.ui.screen

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartViewModel
import com.mandarinkafe.mandarin.features.menu.presentation.view_model.MenuContract
import com.mandarinkafe.mandarin.features.menu.presentation.view_model.MenuViewModel
import com.mandarinkafe.mandarin.navigation.navigateToSearchScreen
import com.mandarinkafe.mandarin.shared.cart.domain.CartMapper.toAddToCartEvent
import com.mandarinkafe.mandarin.shared.cart.domain.CartMapper.toRemoveFromCartNow
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedViewModel
import com.mandarinkafe.mandarin.util.presentation.ui.components.LoadingScreen
import com.mandarinkafe.mandarin.util.presentation.ui.screen.PlaceholderScreen

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

    val favoriteIds by sharedViewModel.favoritesIDs.collectAsState()
    val error = menuSate.error
    val onSharedEvent = sharedViewModel::onEvent
    val onMenuEvent = menuViewModel::onEvent
    val onCartEvent = cartViewModel::onEvent

    when {
        menuSate.isLoading -> LoadingScreen()
        error != null -> PlaceholderScreen(
            error = error,
        )

        else -> MenuContentScreen(
            listState = listState,
            onMenuEvent = onMenuEvent,
            onSharedEvent = onSharedEvent,
            menuSate = menuSate,
            cartState = cartState,
            favoriteIds = favoriteIds,
            onMealDetailsClick = { meal -> onSharedEvent(SharedEvent.OnMealDetailsClick(meal)) },
            onToggleFavorite = { meal -> onSharedEvent(SharedEvent.ToggleFavorite(meal)) },
            onAddToCart = { meal -> onCartEvent(meal.toAddToCartEvent()) },
            onRemoveFromCart = { meal -> onCartEvent(meal.toRemoveFromCartNow()) },
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
