package com.mandarinkafe.mandarin.features.search.presentation.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.features.cart.domain.CartMapper.toAddToCartEvent
import com.mandarinkafe.mandarin.features.cart.domain.CartMapper.toRemoveFromCartNow
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.features.search.presentation.ui.components.MySearchBar
import com.mandarinkafe.mandarin.features.search.presentation.viewmodel.SearchViewModel
import com.mandarinkafe.mandarin.navigation.NavConstants.MENU_SCREEN_ROUTE
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel(),
    cartViewModel: CartViewModel,
    sharedViewModel: SharedViewModel,
    navController: NavController,
    focusSearchBarInput: Boolean = false
) {
    val searchState by searchViewModel.state.collectAsState()
    val cartState by cartViewModel.state.collectAsState()
    val onSharedEvent = sharedViewModel::onEvent
    val onCartEvent = cartViewModel::onEvent
    val favoriteIds by sharedViewModel.favoritesIDs.collectAsState()

    MySearchBar(
        focusSearchBarInput = focusSearchBarInput,
        cartItems = cartState.cartItems,
        favoriteIds = favoriteIds,
        onSearchEvent = searchViewModel::onEvent,
        onSearchDismiss = {
            if (!navController.popBackStack()) {
                navController.navigate(MENU_SCREEN_ROUTE)
            }
        },
        searchState = searchState,
        onMealDetailsClick = { meal -> onSharedEvent(SharedEvent.OnMealDetailsClick(meal)) },
        onToggleFavorite = { meal -> onSharedEvent(SharedEvent.ToggleFavorite(meal)) },
        onAddToCart = { meal -> onCartEvent(meal.toAddToCartEvent()) },
        onRemoveFromCart = { meal -> onCartEvent(meal.toRemoveFromCartNow()) },
    )
}