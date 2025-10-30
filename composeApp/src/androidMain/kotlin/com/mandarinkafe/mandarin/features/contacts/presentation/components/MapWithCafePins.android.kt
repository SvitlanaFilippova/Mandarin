package com.mandarinkafe.mandarin.features.contacts.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.viewinterop.AndroidView
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CENTER_LATITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CENTER_LONGITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_DEFAULT_ZOOM_FOR_CONTACTS_SCREEN
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.BindMapViewToLifecycle
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.CafePinsOnMap
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.CustomMapView
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.MapButtons
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.changeZoom
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.moveCamera
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.mapview.MapView

@Composable
actual fun MapWithCafePins() {
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val mandarinInitPoint =
        Point(MANDARIN_CENTER_LATITUDE, MANDARIN_CENTER_LONGITUDE)

    val onMapReady: (MapView) -> Unit = remember {
        {
            if (mapView == null) {
                mapView = it
                moveCamera(
                    mapView = mapView,
                    point = mandarinInitPoint,
                    zoom = MAP_DEFAULT_ZOOM_FOR_CONTACTS_SCREEN
                )
            }
        }
    }
    var currentZoom by remember { mutableFloatStateOf(MAP_DEFAULT_ZOOM_FOR_CONTACTS_SCREEN) }

    val cameraListener = remember {
        CameraListener { _, cameraPosition, _, _ ->
            currentZoom = cameraPosition.zoom
        }
    }

    // Добавляем слушатель камеры
    LaunchedEffect(mapView) {
        mapView?.mapWindow?.map?.addCameraListener(cameraListener)
    }

    Box(
        modifier = Modifier.Companion
            .fillMaxSize()
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
    ) {
        AndroidView(
            modifier = Modifier.Companion.fillMaxSize(),
            factory = { context ->
                CustomMapView(context)
            }
        ) {
            onMapReady(it)
        }

        // Добавляем пины кафе с динамическим масштабированием
        mapView?.let {
            CafePinsOnMap(mapView = it, currentZoom = currentZoom)
        }
        // Блок с кнопками для управления картой
        MapButtons(
            modifier = Modifier.Companion
                .align(Alignment.Companion.CenterEnd),

            onBackToInitLocationClick = {
                moveCamera(
                    mapView = mapView,
                    point = mandarinInitPoint,
                    zoom = MAP_DEFAULT_ZOOM_FOR_CONTACTS_SCREEN
                )
            },

            onZoomIn = { changeZoom(mapView = mapView, delta = +1f) },
            onZoomOut = { changeZoom(mapView = mapView, delta = -1f) },
        )

    }
    BindMapViewToLifecycle(mapView)
}

