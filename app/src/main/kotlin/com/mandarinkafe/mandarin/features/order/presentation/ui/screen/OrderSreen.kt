package com.mandarinkafe.mandarin.features.order.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.HandleOrderEffects
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.ObserveNavBackstack
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent.StopObservingStatus
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderViewModel
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEffect
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import com.mandarinkafe.mandarin.util.presentation.ui.components.RemoveConfirmationDialog

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrderScreen(
    orderViewModel: OrderViewModel,
    sharedViewModel: SharedViewModel,
    navController: NavHostController
) {
    val state by orderViewModel.state.collectAsState()
    val effectFlow = orderViewModel.effect
    val onEvent = orderViewModel::onEvent
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    var addressIdToDelete by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var showAllAddresses by remember { mutableStateOf(false) }

    // для корректного возврата с экрана добавления адреса
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    ObserveNavBackstack(
        savedStateHandle = currentBackStackEntry?.savedStateHandle,
        onEvent = onEvent
    )

    LaunchedEffect(Unit) {
        onEvent(OrderEvent.GetInitData)
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = { onEvent(OrderEvent.GetInitData) }
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
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
            onBackClick = { navController.popBackStack() }
        )

        PullRefreshIndicator(
            refreshing = state.isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Диалог для подтверждения желания удалить адрес
        if (showConfirmDeleteDialog && addressIdToDelete != null) {
            RemoveConfirmationDialog(
                titleRes = R.string.delete_address_question,
                textRes = R.string.delete_address_text,
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
            navController = navController,
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
            if (effect is SharedEffect.ScrollToTop) {
                scrollState.scrollToItem(0)
            }
        }
    }
}