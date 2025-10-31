package com.mandarinkafe.mandarin.util.presentation.ui.components.map

import YandexMapKit.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.viewinterop.UIKitView
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.address.presentation.ui.models.UiDeliveryArea
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_DEFAULT_ZOOM_FOR_CONTACTS_SCREEN
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.CoreGraphics.CGRectZero
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
fun MapWithDeliveryAreas(
    mapView: YMKMapView?,
    deliveryAreas: List<UiDeliveryArea>,
    displayAddress: String?,
    deliveryArea: UiDeliveryArea?,
    isLoading: Boolean,
    isError: Boolean,
    initLocation: YMKPoint?,
    onCameraMoved: (YMKPoint) -> Unit,
    onMapReady: (YMKMapView) -> Unit,
    locationChosen: Boolean,
    onBackToInitLocationClick: (() -> Unit)?,
    onBackToUserLocationClick: (() -> Unit)? = null,
) {
    val didCallOnMapReady = remember { mutableStateOf(false) }

    val cameraListener = remember {
        object : NSObject(), YMKMapCameraListenerProtocol {
            override fun onCameraPositionChangedWithMap(
                map: YMKMap,
                cameraPosition: YMKCameraPosition,
                cameraUpdateReason: YMKCameraUpdateReason,
                finished: Boolean,
            ) {
                if (finished) {
                    onCameraMoved(cameraPosition.target)
                }
            }
        }
    }

    mapView?.let {
        DeliveryAreasOnMap(
            mapView = it,
            deliveryAreas = deliveryAreas
        )
    }

    // Добавляем слушатель камеры
    LaunchedEffect(mapView) {
        mapView?.mapWindow?.map?.addCameraListenerWithCameraListener(cameraListener)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = Dimens.MarginSmall8)
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
    ) {

        UIKitView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                MapContainerView(frame = CGRectZero.readValue()).apply {
                    moveCamera(
                        mapView = this.mapView,
                        point = initLocation,
                        zoom = MAP_DEFAULT_ZOOM_FOR_CONTACTS_SCREEN
                    )
                }
            },
            update = { view ->
                if (!didCallOnMapReady.value) {
                    didCallOnMapReady.value = true
                    onMapReady(view.mapView)
                }
            },
            onRelease = { view ->
                view.mapView.mapWindow?.map?.removeCameraListenerWithCameraListener(cameraListener)
            }
        )

        // Окно с информацией о текущей зоне доставки
        AnimatedVisibility(
            visible = displayAddress != null && !isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            DeliveryAreaInfo(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(),
                deliveryArea = deliveryArea
            )
        }

        MapButtons(
            modifier = Modifier
                .align(Alignment.CenterEnd),
            onBackToInitLocationClick = onBackToInitLocationClick,
            onBackToUserLocationClick = onBackToUserLocationClick,
            onZoomIn = { changeZoom(mapView = mapView, delta = +1f) },
            onZoomOut = { changeZoom(mapView = mapView, delta = -1f) }
        )

        // Центральный маркер
        val offset = remember { -Dimens.MapPinSize / 2 }
        ChosenLocationPin(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = offset),
            isLoading = isLoading,
            addressFound = locationChosen,
            isError = isError
        )
    }
}
