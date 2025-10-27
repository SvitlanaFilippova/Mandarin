package com.mandarinkafe.mandarin.features.address.presentation.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.presentation.ui.models.UiDeliveryArea
import com.mandarinkafe.mandarin.features.map.MapCameraController

@Composable
actual fun AddressMapContentScreen(
    navController: NavController,
    initAddress: Address?,
    returnToRoute: String,
    initLocation: GeoPoint?,
    userLocation: GeoPoint?,
    deliveryAreas: List<UiDeliveryArea>,
    displayAddress: String?,
    deliveryArea: UiDeliveryArea?,
    isLoading: Boolean,
    locationChosen: Boolean,
    addressValue: String,
    isError: Boolean,
    onCameraMoved: (GeoPoint) -> Unit,
    cameraController: MapCameraController
) {
    TODO ()
}