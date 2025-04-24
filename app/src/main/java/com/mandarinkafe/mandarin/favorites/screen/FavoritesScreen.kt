package com.mandarinkafe.mandarin.favorites.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.cart.ui.view_model.CartViewModel
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.menu.ui.components.MenuList
import com.mandarinkafe.mandarin.menu.ui.components.MenuTopBar
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Event
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuViewModel

@Composable
fun FavoritesScreen(
    menuViewModel: MenuViewModel = hiltViewModel(),
    cartViewModel: CartViewModel,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
    ) {
        MenuTopBar(
            onPhoneClick = { menuViewModel.onEvent(Event.OnPhoneClick) },
            onLogoCLick = { return@MenuTopBar }
        )
        MenuList(
            menuItems = menuViewModel.getFavorites(),
            listState = rememberLazyListState(),
            modifier = Modifier,
            onEvent = menuViewModel::onEvent,
            onCartEvent = cartViewModel::onEvent
        )
    }
}