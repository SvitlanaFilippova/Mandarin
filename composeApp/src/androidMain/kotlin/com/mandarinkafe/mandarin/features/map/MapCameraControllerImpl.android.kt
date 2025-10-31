package com.mandarinkafe.mandarin.features.map

import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.data.Mapper.toYandexPoint
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_DEFAULT_ZOOM_FOR_ADDRESS_SCREEN
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.moveCamera
import com.yandex.mapkit.mapview.MapView

/**
 * Android реализация MapCameraController
 */
class MapCameraControllerImpl(
    private var mapView: MapView?,
) : MapCameraController {

    fun updateMapView(newMapView: MapView?) {
        mapView = newMapView
    }

    override fun moveCamera(point: GeoPoint, zoom: Float?) {
        val yandexPoint = point.toYandexPoint()
        val zoomLevel = zoom ?: MAP_DEFAULT_ZOOM_FOR_ADDRESS_SCREEN
        moveCamera(yandexPoint, mapView, zoomLevel)
    }
}
