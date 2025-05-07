package com.mandarinkafe.mandarin.search.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.cart.ui.view_model.CartViewModel
import com.mandarinkafe.mandarin.search.ui.components.MySearchBar
import com.mandarinkafe.mandarin.search.ui.view_model.SearchViewModel

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
        onSearchDismiss = { navController.popBackStack() },
        focusSearchBarInput = focusSearchBarInput,
        cartState = cartState,
        effectFlow = effectFlow,
        searchState = searchState
    )
}