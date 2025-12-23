package com.mandarinkafe.mandarin.features.delivery.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.address.data.Mapper.toGeoPoint
import com.mandarinkafe.mandarin.features.address.data.Mapper.toYandexPoint
import com.mandarinkafe.mandarin.features.address.presentation.ui.models.UiDeliveryArea
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_DEFAULT_ZOOM_FOR_ADDRESS_SCREEN
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_DEFAULT_ZOOM_FOR_DELIVERY_SCREEN
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.BindMapViewToLifecycle
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.MapWithDeliveryAreas
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.moveCamera
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.rememberLocationPermissionLauncher
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import dev.icerock.moko.resources.compose.stringResource

@Composable
actual fun MapDeliveryScreenContent(
    deliveryAreas: List<UiDeliveryArea>,
    displayAddress: String?,
    deliveryArea: UiDeliveryArea?,
    isLoading: Boolean,
    isError: Boolean,
    initLocation: GeoPoint,
    userLocation: GeoPoint?,
    onCameraMoved: (GeoPoint) -> Unit,
    locationChosen: Boolean,
    onRequestLocation: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    onShowSnackbarWithAction: (String, String, () -> Unit) -> Unit,
    onOpenSettings: () -> Unit,
) {
    var mapView by remember { mutableStateOf<MapView?>(null) }

    val initLocationYandex = initLocation.toYandexPoint()
    val userLocationYandex = remember(userLocation) {
        userLocation?.let { location ->
            Point(
                location.latitude,
                location.longitude
            )
        }
    }

    LaunchedEffect(initLocation, mapView) {
        moveCamera(
            point = initLocationYandex,
            mapView = mapView,
            zoom = MAP_DEFAULT_ZOOM_FOR_DELIVERY_SCREEN
        )
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

    val onBackToInitClick = {
        moveCamera(
            point = initLocationYandex,
            mapView = mapView,
            MAP_DEFAULT_ZOOM_FOR_DELIVERY_SCREEN
        )
    }

    val onBackToUserClick: () -> Unit = {
        if (permissionLauncher.hasPermission()) {
            if (userLocationYandex != null) {
                moveCamera(
                    mapView = mapView,
                    point = userLocationYandex,
                    zoom = MAP_DEFAULT_ZOOM_FOR_ADDRESS_SCREEN
                )
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

    Box(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .height(Dimens.MapHeight)
            .padding(bottom = Dimens.MarginStandard16)
    ) {
        MapWithDeliveryAreas(
            mapView = mapView,
            deliveryAreas = deliveryAreas,
            displayAddress = displayAddress,
            deliveryArea = deliveryArea,
            isLoading = isLoading,
            isError = isError,
            onMapReady = { mapView = it },
            onCameraMoved = { point -> onCameraMoved(point.toGeoPoint()) },
            onBackToInitLocationClick = onBackToInitClick,
            onBackToUserLocationClick = onBackToUserClick,
            locationChosen = locationChosen
        )
    }

    BindMapViewToLifecycle(mapView)
}