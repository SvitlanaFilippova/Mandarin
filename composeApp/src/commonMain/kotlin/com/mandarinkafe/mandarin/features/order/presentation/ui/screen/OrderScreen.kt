package com.mandarinkafe.mandarin.features.order.presentation.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.HandleOrderEffects
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent.StopObservingStatus
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberOrderViewModel
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import com.mandarinkafe.mandarin.util.presentation.ui.components.RemoveConfirmationDialog
import dev.icerock.moko.resources.compose.stringResource
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun OrderScreen(
    navigator: Navigator,
    sharedViewModel: SharedViewModel
) {
    val orderViewModel = rememberOrderViewModel()
    val state by orderViewModel.state.collectAsState()
    val effectFlow = orderViewModel.effect
    val onEvent = orderViewModel::onEvent
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    var addressIdToDelete by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var showAllAddresses by remember { mutableStateOf(false) }

    // TODO! добавить передачу инфо для корректного возврата с экрана добавления адреса
//    val currentBackStackEntry by navigator.currentBackStackEntryAsState()
//
//    ObserveNavBackstack(
//        savedStateHandle = currentBackStackEntry?.savedStateHandle,
//        onEvent = onEvent
//    )

    LaunchedEffect(Unit) {
        onEvent(OrderEvent.GetInitData)
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = { onEvent(OrderEvent.GetInitData) }
    )
    PullRefreshLayout(
        modifier = Modifier.fillMaxSize(),
        state = pullRefreshState
    ) {
        OrderContent(
            state = state,
            onEvent = onEvent,
            scrollState = scrollState,
            coroutineScope = coroutineScope,
            showAllAddresses = showAllAddresses,
            onToggleShowAll = { showAllAddresses = !showAllAddresses },
            onDeleteRequest = {
                addressIdToDelete = it
                showConfirmDeleteDialog = true
            },
            onBackClick = { navigator.popBackStack() }
        )


        // Диалог для подтверждения желания удалить адрес
        if (showConfirmDeleteDialog && addressIdToDelete != null) {
            RemoveConfirmationDialog(
                title = stringResource(MR.strings.delete_address_question),
                text = stringResource(MR.strings.delete_address_text),
                onConfirm = {
                    addressIdToDelete?.let { id ->
                        onEvent(OrderEvent.RemoveAddress(id))
                    }
                    showConfirmDeleteDialog = false
                    addressIdToDelete = null
                },
                onDismiss = {
                    showConfirmDeleteDialog = false
                    addressIdToDelete = null
                }
            )
        }
        val snackbarHostState = LocalSnackbarHostState.current

        HandleOrderEffects(
            effectFlow = effectFlow,
            navController = navigator,
            snackbarHostState = snackbarHostState
        )
        DisposableEffect(Unit) {
            onDispose {
                onEvent(StopObservingStatus)
            }
        }
    }

    // ловим эффект клика по логотипу -> скролим в верхнюю часть экрана
    LaunchedEffect(Unit) {
        sharedViewModel.effect.collect { effect ->
            if (effect is SharedContract.SharedEffect.ScrollToTop) {
                scrollState.scrollToItem(0)
            }
        }
    }
}
