package com.mandarinkafe.mandarin.features.contacts.presentation.components

import YandexMapKit.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CENTER_LATITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CENTER_LONGITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_DEFAULT_ZOOM_FOR_CONTACTS_SCREEN
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.MapButtons
import platform.darwin.NSObject
import androidx.compose.ui.viewinterop.UIKitView
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.CafePinsOnMap
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.MapContainerView
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.changeZoom
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.moveCamera
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.CoreGraphics.CGRectZero

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapWithCafePins() {
    var mapView: YMKMapView? by remember { mutableStateOf(null) }
    val mandarinInitPoint = remember {
        YMKPoint.pointWithLatitude(MANDARIN_CENTER_LATITUDE, MANDARIN_CENTER_LONGITUDE)
    }
    var currentZoom by remember { mutableFloatStateOf(MAP_DEFAULT_ZOOM_FOR_CONTACTS_SCREEN) }

    val cameraListener = remember {
        object : NSObject(), YMKMapCameraListenerProtocol {
            override fun onCameraPositionChangedWithMap(
                map: YMKMap,
                cameraPosition: YMKCameraPosition,
                cameraUpdateReason: YMKCameraUpdateReason,
                finished: Boolean,
            ) {
                currentZoom = cameraPosition.zoom
            }
        }
    }

    LaunchedEffect(mapView) {
        val map = mapView?.mapWindow?.map ?: return@LaunchedEffect
        map.addCameraListenerWithCameraListener(cameraListener)
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.MapHeight)
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
    ) {
        UIKitView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                MapContainerView(frame = CGRectZero.readValue()).apply {
                    mapView = this.mapView
                    moveCamera(
                        mapView = this.mapView,
                        point = mandarinInitPoint,
                        zoom = MAP_DEFAULT_ZOOM_FOR_CONTACTS_SCREEN
                    )
                }
            },
            onRelease = { view ->
                view.mapView.mapWindow?.map?.removeCameraListenerWithCameraListener(cameraListener)
            }
        )

        // Добавляем пины кафе с динамическим масштабированием
        mapView?.let {
            CafePinsOnMap(mapView = it, currentZoom = currentZoom)
        }

        MapButtons(
            modifier = Modifier.align(Alignment.CenterEnd),
            onBackToInitLocationClick = {
                moveCamera(mapView, mandarinInitPoint, MAP_DEFAULT_ZOOM_FOR_CONTACTS_SCREEN)
            },
            onZoomIn = { changeZoom(mapView = mapView, delta = +1f) },
            onZoomOut = { changeZoom(mapView = mapView, delta = -1f) },
        )
    }
}
