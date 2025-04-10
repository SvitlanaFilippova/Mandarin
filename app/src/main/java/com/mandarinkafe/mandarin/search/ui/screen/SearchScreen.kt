package com.mandarinkafe.mandarin.search.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuViewModel
import com.mandarinkafe.mandarin.navigation.navigateToMenuScreen
import com.mandarinkafe.mandarin.search.ui.components.MySearchBar

@Composable
fun SearchScreen(
    viewModel: MenuViewModel = hiltViewModel(),
    navController: NavController,
    focusSearchBarInput: Boolean
) {

    val onMealClick = { navController.navigateToMenuScreen() }
    val state by viewModel.state.collectAsState()
    val filteredMenuItems = state.filteredMenuItems
    val latestSearchText = state.latestSearchText

    MySearchBar(
        filteredMenuItems = filteredMenuItems,
        latestSearchText = latestSearchText,
        onEvent = viewModel::onEvent,
        onMealClick = onMealClick,
        onSearchDismiss = { navController.popBackStack() },
        focusSearchBarInput = focusSearchBarInput,
    )
}