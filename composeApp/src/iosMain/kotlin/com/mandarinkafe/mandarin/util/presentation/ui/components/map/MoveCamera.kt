package com.mandarinkafe.mandarin.util.presentation.ui.components.map

import YandexMapKit.YMKAnimation
import YandexMapKit.YMKAnimationType
import YandexMapKit.YMKCameraPosition
import YandexMapKit.YMKMapView
import YandexMapKit.YMKPoint
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_ANIMATION_DURATION
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_DEFAULT_AZIMUTH
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_DEFAULT_TILT
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_DEFAULT_ZOOM_FOR_ADDRESS_SCREEN
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
fun moveCamera(
    mapView: YMKMapView?,
    point: YMKPoint?,
    zoom: Float = MAP_DEFAULT_ZOOM_FOR_ADDRESS_SCREEN,
    azimuth: Float? = null,
    tilt: Float? = null,
) {
    mapView?.let {
        point?.let {
            mapView.mapWindow?.map?.moveWithCameraPosition(
                YMKCameraPosition.cameraPositionWithTarget(
                    target = point,
                    zoom = zoom,
                    azimuth = azimuth ?: MAP_DEFAULT_AZIMUTH,
                    tilt = tilt ?: MAP_DEFAULT_TILT
                ),
                YMKAnimation.animationWithType(
                    type = YMKAnimationType.YMKAnimationTypeSmooth,
                    duration = MAP_ANIMATION_DURATION
                ),
                null
            )
        }
    }
}