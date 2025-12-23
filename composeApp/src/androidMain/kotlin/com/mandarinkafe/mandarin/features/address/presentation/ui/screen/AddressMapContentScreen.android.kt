package com.mandarinkafe.mandarin.features.address.presentation.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.data.Mapper.toGeoPoint
import com.mandarinkafe.mandarin.features.address.data.Mapper.toYandexPoint
import com.mandarinkafe.mandarin.features.address.presentation.ui.models.UiDeliveryArea
import com.mandarinkafe.mandarin.features.map.MapCameraController
import com.mandarinkafe.mandarin.features.map.MapCameraControllerImpl
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.BindMapViewToLifecycle
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.MapWithDeliveryAreas
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.moveCamera
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.rememberLocationPermissionLauncher
import com.yandex.mapkit.mapview.MapView
import dev.icerock.moko.resources.compose.stringResource

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
    onRequestLocation: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    onShowSnackbarWithAction: (String, String, () -> Unit) -> Unit,
    onOpenSettings: () -> Unit,
) {
    val initLocationYandex = remember(initLocation) { initLocation?.toYandexPoint() }
    val userLocationYandex = remember(userLocation) { userLocation?.toYandexPoint() }

    var mapView by remember { mutableStateOf<MapView?>(null) }

    // Обновляем MapView в контроллере камеры
    LaunchedEffect(mapView) {
        (cameraController as? MapCameraControllerImpl)?.updateMapView(mapView)
    }

    LaunchedEffect(initLocation, userLocation) {
        if (initLocation != null) {
            moveCamera(initLocationYandex, mapView)
        } else {
            moveCamera(userLocationYandex, mapView)
        }
    }

    val permissionDeniedMessage = stringResource(MR.strings.location_permission_denied)
    val permissionDeniedReason = stringResource(MR.strings.location_permission_denied_reason)
    val openSettingsLabel = stringResource(MR.strings.open_settings)

    val permissionLauncher = rememberLocationPermissionLauncher(
        onGranted = {
            onRequestLocation()
        },
        onDenied = {
            onShowSnackbar(permissionDeniedReason)
        }
    )

    val onBackToInitClick = initLocation?.let { { cameraController.moveCamera(it) } }
    val onBackToUserClick: () -> Unit = {
        if (permissionLauncher.hasPermission()) {
            if (userLocation != null) {
                cameraController.moveCamera(userLocation)
            } else {
                onRequestLocation()
            }
        } else {
            if (permissionLauncher.canRequestPermission()) {
                permissionLauncher.requestPermission()
            } else {
                // Разрешение было отклонено и нельзя запросить повторно - показываем snackbar с кнопкой
                onShowSnackbarWithAction(
                    permissionDeniedMessage,
                    openSettingsLabel,
                    onOpenSettings
                )
            }
        }
    }

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
        onBackToUserLocationClick = onBackToUserClick
    )

    BindMapViewToLifecycle(mapView)
}
