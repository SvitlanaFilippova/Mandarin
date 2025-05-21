package com.mandarinkafe.mandarin.features.search.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartViewModel
import com.mandarinkafe.mandarin.features.search.ui.components.MySearchBar
import com.mandarinkafe.mandarin.features.search.ui.view_model.SearchViewModel
import com.mandarinkafe.mandarin.navigation.NavRoutes.MENU_SCREEN_ROUTE

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel(),
    cartViewModel: CartViewModel,
    navController: NavController,
    focusSearchBarInput: Boolean = false
) {
    val searchState by searchViewModel.state.collectAsState()
    val cartState by cartViewModel.state.collectAsState()
    val effectFlow = searchViewModel.effect

    MySearchBar(
        onCartEvent = cartViewModel::onEvent,
        onSearchEvent = searchViewModel::onEvent,
        onSearchDismiss = {
            if (!navController.popBackStack()) {
                navController.navigate(MENU_SCREEN_ROUTE)
            }
        },
        focusSearchBarInput = focusSearchBarInput,
        cartState = cartState,
        effectFlow = effectFlow,
        searchState = searchState
    )
}