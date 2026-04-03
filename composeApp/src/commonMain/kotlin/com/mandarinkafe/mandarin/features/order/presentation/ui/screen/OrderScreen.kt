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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.HandleOrderEffects
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.ObserveNavBackstack
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent.StopObservingStatus
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberOrderViewModel
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import com.mandarinkafe.mandarin.util.presentation.ui.components.dialogs.OrderClosingInfoDialog
import com.mandarinkafe.mandarin.util.presentation.ui.components.dialogs.RemoveConfirmationDialog
import dev.icerock.moko.resources.compose.stringResource
import dev.materii.pullrefresh.PullRefreshIndicator
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState

@Composable
fun OrderScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
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
    var pendingOrderClosingDialog by remember {
        mutableStateOf<OrderEffect.ShowOrderClosingDialog?>(
            null
        )
    }

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
    PullRefreshLayout(
        modifier = Modifier.fillMaxSize(),
        state = pullRefreshState,
        indicator = {
            PullRefreshIndicator(
                state = pullRefreshState,
                contentColor = Colors.Orange,
            )
        }
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
            navController = navController,
            snackbarHostState = snackbarHostState
        )
        DisposableEffect(Unit) {
            onDispose {
                onEvent(StopObservingStatus)
            }
        }
    }

    pendingOrderClosingDialog?.let { dlg ->
        OrderClosingInfoDialog(
            isClosedForWholeDay = dlg.isClosedForWholeDay,
            closingTime = dlg.closingTime,
            onScheduleAnotherDay = {
                pendingOrderClosingDialog = null
                onEvent(OrderEvent.OrderClosingDialogConfirm)
            },
            onDismiss = {
                pendingOrderClosingDialog = null
                onEvent(OrderEvent.OrderClosingDialogDismiss)
            },
        )
    }

    LaunchedEffect(Unit) {
        effectFlow.collect { effect ->
            if (effect is OrderEffect.ShowOrderClosingDialog) {
                pendingOrderClosingDialog = effect
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
