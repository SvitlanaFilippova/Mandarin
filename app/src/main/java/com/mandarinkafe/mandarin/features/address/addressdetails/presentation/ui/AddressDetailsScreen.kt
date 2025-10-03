package com.mandarinkafe.mandarin.features.address.addressdetails.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
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
import com.mandarinkafe.mandarin.navigation.extensions.navigateToAddress
import com.mandarinkafe.mandarin.navigation.extensions.tryGetBackStackEntry
import com.mandarinkafe.mandarin.util.Constants.SHOULD_REFRESH_ADDRESSES_KEY
import com.mandarinkafe.mandarin.util.Constants.SHOULD_SELECT_ADDRESS_ID
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyTextField
import com.mandarinkafe.mandarin.util.presentation.ui.components.RemoveConfirmationDialog
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.BigButtonWithText

@Composable
fun AddressDetailsScreen(
    initAddress: Address?,
    returnToRoute: String,
    isEditMode: Boolean,
    viewModel: AddressDetailsViewModel = hiltViewModel(),
    navController: NavHostController,
    callerEntry: NavBackStackEntry
) {
    if (initAddress == null) return

    val state by viewModel.state.collectAsState()
    // передача в viewModel стартовой информации
    LaunchedEffect(Unit) {
        viewModel.onEvent(AddressDetailsEvent.SetInitAddress(initAddress))
    }
    val effectFlow = viewModel.effect
    val onEvent = viewModel::onEvent
    val isError = state.isError

    var showConfirmDeleteDialog by remember { mutableStateOf(false) }

    val targetEntry = remember(callerEntry, returnToRoute) {
        navController.tryGetBackStackEntry(returnToRoute)
    }

    val noNeedAddressDetails =
        remember(state.address.addressType) { state.address.noNeedAddressDetails }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.MarginSmall8),
    ) {
        ScreenTitleWithBackButton(
            name = stringResource(R.string.address_details_screen_title),
            onBackClick = { navController.popBackStack() }
        )

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

        Spacer(Modifier.height(Dimens.MarginSmall8))

        AddressTypeChooser(
            chosen = state.address.addressType,
            isError = state.isError,
            onItemSelected = { onEvent(AddressDetailsEvent.SetAddressType(it)) }
        )

        // Поле для ввода деталей адреса. Показывается только если выбран способ доставки в квартиру
        with(state.address) {
            ApartmentDetails(
                visible = !noNeedAddressDetails,
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
        Spacer(Modifier.height(Dimens.MarginSmall8))
        // Опциональное поле для примечания к адресу
        MyTextField(
            value = state.address.comment,
            labelRes = R.string.address_comment,
            onValueChange = { onEvent(AddressDetailsEvent.SetAddressComment(it)) }
        )

        // Отступ для кнопок
        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.MarginStandard16)
        ) {
            if (isEditMode) {
                BigButtonWithText(
                    modifier = Modifier.weight(1f),
                    textResID = R.string.delete,
                    onSubmit = { showConfirmDeleteDialog = true },
                    activeContainerColor = Colors.Red
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
                },
                activeContainerColor = Colors.Orange
            )
        }
    }
    // Диалог для подтверждения желания удалить адрес
    if (showConfirmDeleteDialog) {
        RemoveConfirmationDialog(
            titleRes = R.string.delete_address_question,
            textRes = R.string.delete_address_text,
            onConfirm = {
                showConfirmDeleteDialog = false
                onEvent(AddressDetailsEvent.RemoveAddress)

            },
            onDismiss = {
                showConfirmDeleteDialog = false
            }
        )
    }

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is AddressDetailsEffect.EditLocation -> navController.navigateToAddress(
                    effect.address,
                    returnToRoute
                )

                is AddressDetailsEffect.ShowDeleteConfirmDialog -> showConfirmDeleteDialog = true
                is AddressDetailsEffect.GoToParentScreen -> {
                    val selectedId = initAddress.id
                    // Прокидываем флаги в экран-цель, если он в стеке
                    targetEntry?.savedStateHandle?.set(SHOULD_REFRESH_ADDRESSES_KEY, true)
                    if (!isEditMode && selectedId.isNotBlank()) {
                        targetEntry?.savedStateHandle?.set(SHOULD_SELECT_ADDRESS_ID, selectedId)
                    } else {
                        // на всякий случай очищаем, если редактировали
                        targetEntry?.savedStateHandle?.set(SHOULD_SELECT_ADDRESS_ID, null)
                    }
                    // Возврат на нужный экран
                    navController.popBackStack(returnToRoute, inclusive = false)

                }
            }
        }
    }
}