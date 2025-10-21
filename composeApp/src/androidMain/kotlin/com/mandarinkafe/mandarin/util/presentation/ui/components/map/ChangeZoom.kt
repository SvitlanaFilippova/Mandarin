package com.mandarinkafe.mandarin.util.presentation.ui.components.map

import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_ANIMATION_DURATION
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_MAX_ZOOM
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_MIN_ZOOM
import com.yandex.mapkit.Animation
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

fun changeZoom(mapView: MapView?, delta: Float) {
    val position = mapView?.mapWindow?.map?.cameraPosition ?: return
    val newZoom = (position.zoom + delta).coerceIn(MAP_MIN_ZOOM, MAP_MAX_ZOOM)

    mapView.mapWindow.map.move(
        CameraPosition(
            position.target,
            newZoom,
            position.azimuth,
            position.tilt
        ),
        Animation(Animation.Type.SMOOTH, MAP_ANIMATION_DURATION),
        null
    )
}