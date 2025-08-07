package com.mandarinkafe.mandarin.features.order.presentation.ui.screen

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent.StopObservingStatus
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderViewModel
import com.mandarinkafe.mandarin.navigation.NavConstants
import com.mandarinkafe.mandarin.navigation.extensions.navigateToAddress
import com.mandarinkafe.mandarin.navigation.extensions.navigateToAddressDetails
import com.mandarinkafe.mandarin.navigation.extensions.navigateToOrderInfo
import com.mandarinkafe.mandarin.util.Constants.SHOULD_REFRESH_ADDRESSES_KEY
import com.mandarinkafe.mandarin.util.Constants.SHOULD_SELECT_ADDRESS_ID
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import com.mandarinkafe.mandarin.util.presentation.ui.components.ConfirmationDialog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OrderScreen(
    orderViewModel: OrderViewModel,
    navController: NavHostController
) {
    val state by orderViewModel.state.collectAsState()
    val effectFlow = orderViewModel.effect
    val onEvent = orderViewModel::onEvent
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    var addressToDelete by remember { mutableStateOf<String?>(null) }
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
        orderViewModel.onEvent(OrderEvent.GetPaymentTypes)
    }

    OrderContent(
        state = state,
        onEvent = onEvent,
        scrollState = scrollState,
        coroutineScope = coroutineScope,
        showAllAddresses = showAllAddresses,
        onToggleShowAll = { showAllAddresses = !showAllAddresses },
        onDeleteRequest = {
            addressToDelete = it
            showConfirmDeleteDialog = true
        }
    )

    // Диалог для подтверждения желания удалить адрес
    if (showConfirmDeleteDialog && addressToDelete != null) {
        ConfirmationDialog(
            titleRes = R.string.delete_address_question,
            textRes = R.string.delete_address_text,
            onConfirm = {
                addressToDelete?.let { id ->
                    onEvent(OrderEvent.RemoveAddress(id))
                }
                showConfirmDeleteDialog = false
                addressToDelete = null
            },
            onDismiss = {
                showConfirmDeleteDialog = false
                addressToDelete = null
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

@Composable
private fun HandleOrderEffects(
    effectFlow: Flow<OrderEffect>,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is OrderEffect.AddNewAddress -> navController.navigateToAddress()
                is OrderEffect.EditAddress ->
                    navController.navigateToAddressDetails(
                        effect.address,
                        isEditMode = true,
                        backTargetRoute = NavConstants.ORDER_SCREEN_ROUTE
                    )

                is OrderEffect.ShowError ->
                    snackbarHostState.showSnackbar("Ошибка: ${effect.message}")

                is OrderEffect.ShowSuccess ->
                    navController.navigateToOrderInfo(effect.orderId)
            }
        }
    }
}

@Composable
private fun ObserveNavBackstack(
    savedStateHandle: SavedStateHandle?,
    onEvent: (OrderEvent) -> Unit
) {
    val shouldRefreshFlow = remember(savedStateHandle) {
        savedStateHandle?.getStateFlow(SHOULD_REFRESH_ADDRESSES_KEY, false)
    }

    val selectedAddressIdFlow = remember(savedStateHandle) {
        savedStateHandle?.getStateFlow<String?>(SHOULD_SELECT_ADDRESS_ID, null)
    }

    val shouldRefresh by shouldRefreshFlow?.collectAsState() ?: remember { mutableStateOf(false) }
    val selectedAddressId by selectedAddressIdFlow?.collectAsState() ?: remember {
        mutableStateOf(
            null
        )
    }

    // Реагируем на refresh
    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            onEvent(OrderEvent.RefreshAddresses)
            savedStateHandle?.set(SHOULD_REFRESH_ADDRESSES_KEY, false)
        }
    }

    // Реагируем на выбор адреса
    LaunchedEffect(selectedAddressId) {
        selectedAddressId?.let { id ->
            onEvent(OrderEvent.SelectAddressById(id))
            savedStateHandle?.set(SHOULD_SELECT_ADDRESS_ID, null)
        }
    }
}