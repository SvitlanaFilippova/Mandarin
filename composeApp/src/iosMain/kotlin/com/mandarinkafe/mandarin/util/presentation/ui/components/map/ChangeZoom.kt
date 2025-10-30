package com.mandarinkafe.mandarin.util.presentation.ui.components.map

import YandexMapKit.YMKMapView
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_MAX_ZOOM
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_MIN_ZOOM
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
fun changeZoom(mapView: YMKMapView?, delta: Float) {
    val position = mapView?.mapWindow?.map?.cameraPosition ?: return
    val newZoom = (position.zoom + delta).coerceIn(MAP_MIN_ZOOM, MAP_MAX_ZOOM)
    moveCamera(
        mapView = mapView,
        point = position.target,
        zoom = newZoom,
        azimuth = position.azimuth,
        tilt = position.tilt
    )
}