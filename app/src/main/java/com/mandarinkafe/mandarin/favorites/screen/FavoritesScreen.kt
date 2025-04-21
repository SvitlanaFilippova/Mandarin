package com.mandarinkafe.mandarin.favorites.screen

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.menu.ui.components.MenuList
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuViewModel

@Composable
fun FavoritesScreen(
    viewModel: MenuViewModel = hiltViewModel()
) {

    val favoritesList = viewModel.getFavorites()

    MenuList(
        menuItems = favoritesList,
        listState = rememberLazyListState(),
        modifier = Modifier,
        onEvent = viewModel::onEvent,
    )
}