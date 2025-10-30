package com.mandarinkafe.mandarin.features.delivery.presentation.ui.components

import YandexMapKit.YMKMapView
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
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.address.data.Mapper.toGeoPoint
import com.mandarinkafe.mandarin.features.address.data.Mapper.toYandexPoint
import com.mandarinkafe.mandarin.features.address.presentation.ui.models.UiDeliveryArea
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_DEFAULT_ZOOM_FOR_DELIVERY_SCREEN
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.MapWithDeliveryAreas
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.moveCamera
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapDeliveryScreenContent(
    deliveryAreas: List<UiDeliveryArea>,
    displayAddress: String?,
    deliveryArea: UiDeliveryArea?,
    isLoading: Boolean,
    isError: Boolean,
    initLocation: GeoPoint,
    onCameraMoved: (GeoPoint) -> Unit,
    locationChosen: Boolean,
) {
    var mapView: YMKMapView? by remember { mutableStateOf(null) }
    val initLocationYandex = initLocation.toYandexPoint()

    LaunchedEffect(initLocation, mapView) {
        moveCamera(
            mapView = mapView,
            point = initLocationYandex,
            zoom = MAP_DEFAULT_ZOOM_FOR_DELIVERY_SCREEN
        )
    }

    Box(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .height(Dimens.MapOnDeliveryScreenHeight)
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
            locationChosen = locationChosen,
            initLocation = initLocationYandex,
            onBackToInitLocationClick = {
                moveCamera(
                    point = initLocationYandex,
                    mapView = mapView,
                    zoom = MAP_DEFAULT_ZOOM_FOR_DELIVERY_SCREEN
                )
            },
        )
    }
}
