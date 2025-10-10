package com.mandarinkafe.mandarin.features.savedadresses.presentation.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.savedadresses.presentation.ui.components.HandleSavedAddressesEffects
import com.mandarinkafe.mandarin.features.savedadresses.presentation.ui.components.SavedAddressCard
import com.mandarinkafe.mandarin.features.savedadresses.presentation.viewmodel.SavedAddressesContract.SavedAddressesEvent
import com.mandarinkafe.mandarin.features.savedadresses.presentation.viewmodel.SavedAddressesViewModel
import com.mandarinkafe.mandarin.util.Constants.SHOULD_REFRESH_ADDRESSES_KEY
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyClickableText
import com.mandarinkafe.mandarin.util.presentation.ui.components.RemoveConfirmationDialog
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import com.mandarinkafe.mandarin.util.presentation.ui.components.TooltipText
import org.koin.androidx.compose.koinViewModel

@Composable
fun SavedAddressesScreen(
    navController: NavHostController,
    viewModel: SavedAddressesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val effectFlow = viewModel.effect
    val addresses = state.data
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    var addressIdToDelete by remember { mutableStateOf<String?>(null) }

    // для корректного возврата с экрана добавления адреса
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val onEvent = viewModel::onEvent

    ObserveNavBackstack(
        savedStateHandle = currentBackStackEntry?.savedStateHandle,
        onEvent = onEvent
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.MarginSmall8),
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)

    ) {
        item {
            ScreenTitleWithBackButton(
                name = stringResource(R.string.saved_addresses),
                onBackClick = { navController.popBackStack() }
            )
        }

        if (addresses.isNotEmpty()) {
            items(items = addresses) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {}),
                    colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)
                ) {
                    SavedAddressCard(
                        modifier = Modifier.padding(Dimens.MarginStandard16),
                        address = it,
                        onAddressChosen = { onEvent(SavedAddressesEvent.EditAddress(it)) },
                        onEditAddress = { onEvent(SavedAddressesEvent.EditAddress(it)) },
                        onRemoveAddress = {
                            addressIdToDelete = it.id
                            showConfirmDeleteDialog = true
                        },
                    )
                }
            }
        } else {
            item {
                TooltipText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = Dimens.MarginSmall8,
                            vertical = Dimens.MarginStandard16
                        ),
                    textRes = R.string.no_saved_addresses
                )
            }
        }
        item {
            MyClickableText(
                textRes = R.string.add_address,
                onClick = { onEvent(SavedAddressesEvent.AddNewAddress) }
            )
        }
    }

    // Диалог для подтверждения желания удалить адрес
    if (showConfirmDeleteDialog && addressIdToDelete != null) {
        RemoveConfirmationDialog(
            titleRes = R.string.delete_address_question,
            textRes = R.string.delete_address_text,
            onConfirm = {
                addressIdToDelete?.let { id ->
                    onEvent(SavedAddressesEvent.RemoveAddress(id))
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

    HandleSavedAddressesEffects(
        effectFlow = effectFlow,
        navController = navController,
        snackbarHostState = snackbarHostState
    )
}

@Composable
private fun ObserveNavBackstack(
    savedStateHandle: SavedStateHandle?,
    onEvent: (SavedAddressesEvent) -> Unit
) {
    val shouldRefreshFlow = remember(savedStateHandle) {
        savedStateHandle?.getStateFlow(SHOULD_REFRESH_ADDRESSES_KEY, true)
    }

    shouldRefreshFlow?.let { flow ->
        val shouldRefresh by flow.collectAsState()

        LaunchedEffect(shouldRefresh) {
            if (shouldRefresh) {
                onEvent(SavedAddressesEvent.RefreshAddresses)
                savedStateHandle?.set(SHOULD_REFRESH_ADDRESSES_KEY, false)
            }
        }
    }
}