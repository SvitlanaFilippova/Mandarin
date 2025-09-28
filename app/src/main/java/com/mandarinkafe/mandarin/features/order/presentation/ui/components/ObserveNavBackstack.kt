package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.SavedStateHandle
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEvent
import com.mandarinkafe.mandarin.util.Constants.SHOULD_REFRESH_ADDRESSES_KEY
import com.mandarinkafe.mandarin.util.Constants.SHOULD_SELECT_ADDRESS_ID

@Composable
fun ObserveNavBackstack(
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