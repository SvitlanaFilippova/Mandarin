package com.mandarinkafe.mandarin.menu.ui.screen

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.menu.ui.MenuContract
import com.mandarinkafe.mandarin.menu.ui.MenuViewModel

@Composable
fun MenuScreen(viewModel: MenuViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val effectFlow = viewModel.effect
    val listState = rememberLazyListState()

    when {
        state.isLoading -> LoadingScreen()
        state.errorMessage != null -> ErrorScreen(state.errorMessage)
        else -> MenuContentScreen(
            menuItems = state.menuItems,
            listState = listState,
            selectedTabIndex = state.selectedTabIndex,
            selectedSubTabIndex = state.selectedSubTabIndex,
            selectedBannerIndex = state.selectedBannerIndex,
            onEvent = viewModel::onEvent
        )
    }

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is MenuContract.Effect.ShowSnackbar -> {
                    // Показываем снекбар
                }

                is MenuContract.Effect.NavigateTo -> {
                    // Навигация
                }
            }
        }
    }
}