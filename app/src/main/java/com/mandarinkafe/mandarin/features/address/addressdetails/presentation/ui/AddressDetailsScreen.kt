package com.mandarinkafe.mandarin.features.address.addressdetails.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.components.AddressTypeChooser
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailsEffect
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailsEvent
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsViewModel
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.ApartmentDetails
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.LocationIcon
import com.mandarinkafe.mandarin.navigation.NavConstants.ORDER_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.extensions.navigateToAddress
import com.mandarinkafe.mandarin.util.presentation.ui.components.ConfirmationDialog
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyTextField
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.BigButtonWithText

@Composable
fun AddressDetailsScreen(
    initAddress: Address?,
    isEditMode: Boolean,
    viewModel: AddressDetailsViewModel = hiltViewModel(),
    navController: NavHostController
) {
    if (initAddress == null) return

    val state by viewModel.state.collectAsState()
    // передача в viewModel стартовой информации
    LaunchedEffect(Unit) {
        viewModel.onEvent(AddressDetailsEvent.SetAddress(initAddress))
    }
    val effectFlow = viewModel.effect
    val onEvent = viewModel::onEvent
    val isError = state.isError

    var showConfirmDeleteDialog by remember { mutableStateOf(false) }

    val parentEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(ORDER_SCREEN_ROUTE)
    }
    val parentSavedStateHandle = parentEntry.savedStateHandle
    val backToOrderScreen = {
        parentSavedStateHandle["shouldRefreshAddresses"] = true
        navController.popBackStack(ORDER_SCREEN_ROUTE, inclusive = false)
    }
    val noNeedAddressDetails =
        remember(state.address.addressType) { state.address.noNeedAddressDetails }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.MarginSmall8),
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
        // Улица и дом. При клике - переход на экран выбора адреса на карте
        MyTextField(
            modifier = Modifier.clickable(onClick = { onEvent(AddressDetailsEvent.ChangeLocation) }),
            enabled = false,
            minLines = 2,
            isError = isError && state.address.streetAndBuilding.isEmpty(),
            value = state.address.streetAndBuilding,
            labelRes = R.string.street_and_building,
            leadingIcon = {
                LocationIcon(enabled = false)
            }
        )

        AddressTypeChooser(
            chosen = state.address.addressType,
            isError = state.isError,
            onItemSelected = { onEvent(AddressDetailsEvent.SetAddressType(it)) }
        )
        // Поле для ввода деталей адреса показываем только если выбран способ доставки в квартиру
        if (!noNeedAddressDetails) {
            with(state.address) {
                ApartmentDetails(
                    isError = isError,
                    apartmentNumberQuery = apartmentNumber,
                    apartmentEntranceQuery = entrance,
                    apartmentFloorQuery = floor,
                    apartmentIntercomQuery = intercom,
                    onApartmentNumberEntered = { onEvent(AddressDetailsEvent.SetApartmentNumber(it)) },
                    onEntranceEntered = { onEvent(AddressDetailsEvent.SetEntrance(it)) },
                    onFloorEntered = { onEvent(AddressDetailsEvent.SetFloor(it)) },
                    onIntercomEntered = { onEvent(AddressDetailsEvent.SetIntercom(it)) }
                )
            }
        }
        // Опциональное поле для примечания к адресу
        MyTextField(
            value = state.address.comment,
            labelRes = R.string.address_comment,
            onValueChange = { onEvent(AddressDetailsEvent.SetAddressComment(it)) }
        )

        // Отступ для кнопок
        Spacer(Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth()) {
            if (isEditMode) {
                BigButtonWithText(
                    modifier = Modifier.weight(1f),
                    textResID = R.string.delete,
                    onSubmit = { showConfirmDeleteDialog = true },
                    activeContainerColor = Colors.ErrorRed
                )
                Spacer(Modifier.width(Dimens.MarginStandard16))
            }

            BigButtonWithText(
                modifier = Modifier.weight(1f),
                textResID = R.string.save,
                shouldBeActive = state.addressIsValid,
                onMissingRequiredInfo = {
                    onEvent(
                        AddressDetailsEvent.OnMissingRequiredInfo
                    )
                },
                onSubmit = {
                    onEvent(AddressDetailsEvent.SaveAddress)
                    backToOrderScreen()

                },
                activeContainerColor = Colors.Orange
            )
        }
    }
    // Диалог для подтверждения желания удалить адрес
    if (showConfirmDeleteDialog) {
        ConfirmationDialog(
            titleRes = R.string.delete_address_question,
            textRes = R.string.delete_address_text,
            onConfirm = {
                showConfirmDeleteDialog = false
                onEvent(AddressDetailsEvent.RemoveAddress)
                backToOrderScreen()
            },
            onDismiss = {
                showConfirmDeleteDialog = false
            }
        )
    }

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is AddressDetailsEffect.EditLocation -> navController.navigateToAddress(effect.address)
                is AddressDetailsEffect.ShowDeleteConfirmDialog -> showConfirmDeleteDialog = true
            }
        }
    }
}
