package com.mandarinkafe.mandarin.search.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.cart.ui.view_model.CartViewModel
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuViewModel
import com.mandarinkafe.mandarin.search.ui.components.MySearchBar

@Composable
fun SearchScreen(
    menuViewModel: MenuViewModel = hiltViewModel(),
    cartViewModel: CartViewModel,
    navController: NavController,
    focusSearchBarInput: Boolean
) {

    val menuState by menuViewModel.state.collectAsState()
    val cartState by cartViewModel.state.collectAsState()
    val filteredMenuItems = menuState.filteredMenuItems
    val latestSearchText = menuState.latestSearchText
    val effectFlow = menuViewModel.effect

    MySearchBar(
        filteredMenuItems = filteredMenuItems,
        latestSearchText = latestSearchText,
        onMenuEvent = menuViewModel::onEvent,
        onCartEvent = cartViewModel::onEvent,
        onSearchDismiss = { navController.popBackStack() },
        focusSearchBarInput = focusSearchBarInput,
        cartState = cartState,
        effectFlow = effectFlow,
    )
}