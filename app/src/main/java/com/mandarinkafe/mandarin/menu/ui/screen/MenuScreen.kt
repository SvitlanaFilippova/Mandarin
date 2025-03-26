package com.mandarinkafe.mandarin.menu.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.mandarinkafe.mandarin.menu.ui.MenuIntent
import com.mandarinkafe.mandarin.menu.ui.MenuViewModel
import com.mandarinkafe.mandarin.menu.ui.MenuViewState

@Composable
fun MenuScreen(viewModel: MenuViewModel) {
    val state by viewModel.state.observeAsState(MenuViewState.Loading)

    LaunchedEffect(Unit) {
        viewModel.handleIntent(MenuIntent.LoadMenu)
    }

    when (state) {
        is MenuViewState.Loading -> LoadingScreen()
        is MenuViewState.Error -> ErrorScreen()
        is MenuViewState.Content -> {
            val content = state as MenuViewState.Content
            MenuContentScreen(
                menuItems = content.menuItems,
                selectedTabIndex = content.selectedTabIndex,
                selectedSubTabIndex = content.selectedSubTabIndex,
                onCategorySelected = { index ->
                    viewModel.handleIntent(MenuIntent.SelectCategory(index))
                },
                onSubCategorySelected = { index ->
                    viewModel.handleIntent(MenuIntent.SelectSubCategory(index))
                },
                onToggleFavorite = { meal ->
                    viewModel.handleIntent(MenuIntent.ToggleFavorite(meal))
                },
                onAddToCart = { meal ->
                    viewModel.handleIntent(MenuIntent.AddToCart(meal))
                },
                onRemoveFromCart = { meal ->
                    viewModel.handleIntent(MenuIntent.RemoveFromCart(meal))
                }
            )
        }
    }
}
