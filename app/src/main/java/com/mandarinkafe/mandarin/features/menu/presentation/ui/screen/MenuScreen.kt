package com.mandarinkafe.mandarin.features.menu.presentation.ui.screen

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.core.domain.mapper.Mapper.toCustomizedMeal
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.features.menu.presentation.viewmodel.MenuContract
import com.mandarinkafe.mandarin.features.menu.presentation.viewmodel.MenuViewModel
import com.mandarinkafe.mandarin.navigation.extensions.navigateToSearchScreen
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel
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
            onRetryClick = { onMenuEvent(MenuContract.MenuEvent.ForceRefresh) },
            onCallClick = { onSharedEvent(SharedEvent.OnPhoneClick) },
        )
        else -> MenuContentScreen(
            listState = listState,
            onMenuEvent = onMenuEvent,
            onSharedEvent = onSharedEvent,
            cartItems = cartState.cartItems,
            inProgressItems = cartState.inProgressItems,
            menuItems = menuSate.menuItems,
            banners = menuSate.banners,
            bannersAreLoading = menuSate.bannersAreLoading,
            selectedTabIndex = menuSate.selectedTabIndex,
            selectedSubTabIndex = menuSate.selectedSubTabIndex,
            selectedMenuItemIndex = menuSate.selectedMenuItemIndex,
            favoriteIds = favoriteIds,
            onMealDetailsClick = { meal -> onSharedEvent(SharedEvent.OnMealDetailsClick(meal)) },
            onToggleFavorite = { meal -> onSharedEvent(SharedEvent.ToggleFavorite(meal)) },
            onAddToCart = { meal -> onCartEvent(CartEvent.AddToCart(customizedMeal = meal.toCustomizedMeal())) },
            onRemoveFromCart = { meal -> onCartEvent(CartEvent.OnReduce(meal = meal)) },
        )

    }

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            if (effect is MenuContract.MenuEffect.OpenSearch
            ) {
                navController.navigateToSearchScreen(effect.focusSearch)
            }
        }
    }
}
