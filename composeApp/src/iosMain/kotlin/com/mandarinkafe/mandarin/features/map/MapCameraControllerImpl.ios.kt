package com.mandarinkafe.mandarin.features.map

import YandexMapKit.YMKMapView
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.data.Mapper.toYandexPoint
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_DEFAULT_ZOOM_FOR_ADDRESS_SCREEN
import kotlinx.cinterop.ExperimentalForeignApi

/**
 * ios реализация MapCameraController
 */
@OptIn(ExperimentalForeignApi::class)
class MapCameraControllerImpl(
    private var mapView: YMKMapView?,
) : MapCameraController {

    fun updateMapView(newMapView: YMKMapView?) {
        mapView = newMapView
    }

    override fun moveCamera(point: GeoPoint, zoom: Float?) {
        val yandexPoint = point.toYandexPoint()
        val zoomLevel = zoom ?: MAP_DEFAULT_ZOOM_FOR_ADDRESS_SCREEN
        com.mandarinkafe.mandarin.util.presentation.ui.components.map.moveCamera(
            mapView = mapView,
            point = yandexPoint,
            zoom = zoomLevel
        )
    }
}
