package com.mandarinkafe.mandarin.features.addressdetails.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.features.addressdetails.presentation.viewmodel.AddressDetailsViewModel
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.DeliveryInfo

@Composable
fun AddressDetailsScreen(
    viewModel: AddressDetailsViewModel = hiltViewModel(),
    navController: NavHostController
) {
    viewModel.effect
    viewModel::onEvent


    DeliveryInfo(
        chosen = TODO(),
        onDeliverySelected = TODO(),
        isError = TODO(),
        isPrivateHouse = TODO(),
        isPrivateHouseToggled = TODO(),
        addressQuery = TODO(),
        onAddressEntered = TODO(),
        addressComment = TODO(),
        onAddressCommentsEntered = TODO(),
        apartmentNumberQuery = TODO(),
        onApartmentNumberEntered = TODO(),
        apartmentEntranceQuery = TODO(),
        onEntranceEntered = TODO(),
        apartmentFloorQuery = TODO(),
        onFloorEntered = TODO(),
        apartmentIntercomQuery = TODO(),
        onIntercomEntered = TODO(),
        onGetLocationIconClick = TODO()
    )
}