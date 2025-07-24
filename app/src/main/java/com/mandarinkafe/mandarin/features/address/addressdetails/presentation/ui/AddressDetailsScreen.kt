package com.mandarinkafe.mandarin.features.address.addressdetails.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.components.HandleAddressDetailsEffects
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailsEvent
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsViewModel
import com.mandarinkafe.mandarin.features.order.presentation.models.UiAddress
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.ApartmentDetails
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.GetLocationIcon
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyTextField
import com.mandarinkafe.mandarin.util.presentation.ui.components.SwitchWithTextRow
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.BigButtonWithText

@Composable
fun AddressDetailsScreen(
    initAddress: UiAddress?,
    isEditMode: Boolean,
    viewModel: AddressDetailsViewModel = hiltViewModel(),
    navController: NavHostController
) {
    if (initAddress == null) return
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.onEvent(AddressDetailsEvent.SetAddress(initAddress))
    }
    val onEvent = viewModel::onEvent
    val isPrivateHouse = state.address.isPrivateHouse
    val isError = state.isError
    val requestApartmentDetails by remember(
        isPrivateHouse
    ) { mutableStateOf(!isPrivateHouse) }

    // Улица и дом. При клике - переход на экран "локация"
    MyTextField(
        modifier = Modifier.clickable(onClick = { onEvent(AddressDetailsEvent.ChangeLocation) }),
        isError = isError && state.address.streetAndBuilding.isEmpty(),
        value = state.address.streetAndBuilding,
        labelRes = R.string.your_address,
        leadingIcon = {
            GetLocationIcon(
                onClick = { onEvent(AddressDetailsEvent.ChangeLocation) }
            )
        }
    )

    SwitchWithTextRow(
        value = isPrivateHouse,
        onValueChange = { onEvent(AddressDetailsEvent.IsPrivateHouseToggled(it)) },
        textRes = R.string.private_house
    )

    // Поле для ввода деталей адреса показываем только если выбран способ доставки в квартиру
    if (requestApartmentDetails) {
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

    BigButtonWithText(
        textResID = R.string.save_address,
        onMissingRequiredInfo = {},
        onSubmit = { }
    )

    HandleAddressDetailsEffects(
        effectFlow = viewModel.effect,
        navController = navController
    )

}
