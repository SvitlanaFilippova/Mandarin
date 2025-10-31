package com.mandarinkafe.mandarin.features.address.presentation.ui.screen

import YandexMapKit.YMKMapView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.data.Mapper.toGeoPoint
import com.mandarinkafe.mandarin.features.address.data.Mapper.toYandexPoint
import com.mandarinkafe.mandarin.features.address.presentation.ui.models.UiDeliveryArea
import com.mandarinkafe.mandarin.features.map.MapCameraController
import com.mandarinkafe.mandarin.features.map.MapCameraControllerImpl
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.MapWithDeliveryAreas
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.moveCamera
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
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
    cameraController: MapCameraController,
) {
    val initLocationYandex = remember(initLocation) { initLocation?.toYandexPoint() }
    val userLocationYandex = remember(userLocation) { userLocation?.toYandexPoint() }

    var mapView by remember { mutableStateOf<YMKMapView?>(null) }

    // Обновляем MapView в контроллере камеры
    LaunchedEffect(mapView) {
        (cameraController as? MapCameraControllerImpl)?.updateMapView(mapView)
    }

    LaunchedEffect(initLocation, userLocation) {
        if (initLocation != null) {
            moveCamera(mapView = mapView, point = initLocationYandex)
        } else {
            moveCamera(mapView = mapView, point = userLocationYandex)
        }
    }

    val onBackToInitClick = initLocation?.let { { cameraController.moveCamera(it) } }
    val onBackToUserClick = userLocation?.let { { cameraController.moveCamera(it) } }

    MapWithDeliveryAreas(
        mapView = mapView,
        deliveryAreas = deliveryAreas,
        displayAddress = displayAddress,
        deliveryArea = deliveryArea,
        isLoading = isLoading,
        locationChosen = locationChosen,
        isError = isError,
        onMapReady = { mapView = it },
        onCameraMoved = { onCameraMoved(it.toGeoPoint()) },
        onBackToInitLocationClick = onBackToInitClick,
        onBackToUserLocationClick = onBackToUserClick,
        initLocation = initLocationYandex
    )
}