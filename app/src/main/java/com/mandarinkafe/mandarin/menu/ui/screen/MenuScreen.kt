package com.mandarinkafe.mandarin.menu.ui.screen

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuViewModel

@Composable
fun MenuScreen(viewModel: MenuViewModel = hiltViewModel(), onSearchClick: () -> Unit) {
    val state by viewModel.state.collectAsState()
    val effectFlow = viewModel.effect
    val listState = rememberLazyListState()

    when {
        state.isLoading -> LoadingScreen()
        state.errorMessage != null -> ErrorScreen(state.errorMessage)
        else -> MenuContentScreen(
            listState = listState,
            onEvent = viewModel::onEvent,
            state = state
        )
    }

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is MenuContract.Effect.ShowSnackbar -> {
                    // Показываем снекбар
                }

                is MenuContract.Effect.OpenMealCustomization -> {
                    // Обработка клика по кнопке кастомизации
                }

                is MenuContract.Effect.OpenSearch -> {
                    onSearchClick()
                }
            }
        }
    }
}