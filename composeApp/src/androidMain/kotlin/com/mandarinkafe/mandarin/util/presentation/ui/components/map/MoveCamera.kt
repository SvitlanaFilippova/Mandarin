package com.mandarinkafe.mandarin.util.presentation.ui.components.map

import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_ANIMATION_DURATION
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_DEFAULT_AZIMUTH
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_DEFAULT_TILT
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_DEFAULT_ZOOM_FOR_ADDRESS_SCREEN
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_DEFAULT_ZOOM_FOR_DELIVERY_SCREEN
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

fun moveCamera(point: Point?, mapView: MapView?, zoom: Float = MAP_DEFAULT_ZOOM_FOR_ADDRESS_SCREEN) {
    point?.let {
        mapView?.mapWindow?.map?.move(
            CameraPosition(
                /* target = */ point,
                /* zoom = */ zoom,
                /* azimuth = */ MAP_DEFAULT_AZIMUTH,
                /* tilt = */ MAP_DEFAULT_TILT
            ),
            Animation(Animation.Type.SMOOTH, MAP_ANIMATION_DURATION),
            null
        )
    }
}