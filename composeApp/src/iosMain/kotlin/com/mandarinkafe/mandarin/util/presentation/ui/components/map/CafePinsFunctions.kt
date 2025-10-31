@file:OptIn(ExperimentalForeignApi::class)

package com.mandarinkafe.mandarin.util.presentation.ui.components.map

import YandexMapKit.YMKIconStyle
import YandexMapKit.YMKMapView
import YandexMapKit.YMKPoint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.mandarinkafe.mandarin.features.map.calculatePinScale
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CAFE_LATITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CAFE_LONGITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_PIZZERIA_LATITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_PIZZERIA_LONGITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_ANCHOR_X
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_ANCHOR_Y
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_OPACITY
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGPointMake
import platform.Foundation.NSNumber
import platform.Foundation.NSValue
import platform.Foundation.numberWithDouble
import platform.UIKit.UIImage
import platform.UIKit.valueWithCGPoint

@Composable
fun CafePinsOnMap(mapView: YMKMapView, currentZoom: Float) {
    LaunchedEffect(mapView, currentZoom) {
        clearCafePins(mapView)
        addCafePins(mapView, currentZoom)
    }
}

private fun clearCafePins(
    mapView: YMKMapView,
) {
    val mapObjects = mapView.mapWindow?.map?.mapObjects
    mapObjects?.clear()
}

private fun addCafePins(mapView: YMKMapView, currentZoom: Float) {
    val mapObjects = mapView.mapWindow?.map?.mapObjects ?: return

    val cafePoint = YMKPoint.pointWithLatitude(MANDARIN_CAFE_LATITUDE, MANDARIN_CAFE_LONGITUDE)
    val pizzeriaPoint =
        YMKPoint.pointWithLatitude(MANDARIN_PIZZERIA_LATITUDE, MANDARIN_PIZZERIA_LONGITUDE)

    val cafeIcon = UIImage.imageNamed("map_pin_cafe")
    val pizzaIcon = UIImage.imageNamed("map_pin_pizza")

    val dynamicScale = calculatePinScale(currentZoom).toDouble()

    fun addPin(point: YMKPoint, icon: UIImage?) {
        icon?.let {
            val iconStyle = YMKIconStyle().apply {
                setAnchor(
                    NSValue.valueWithCGPoint(
                        CGPointMake(
                            PIN_ANCHOR_X.toDouble(),
                            PIN_ANCHOR_Y.toDouble()
                        )
                    )
                )
                setScale(NSNumber.numberWithDouble(dynamicScale))
            }

            mapObjects.addPlacemarkWithPoint(
                point = point, image = icon, style = iconStyle
            ).apply { setOpacity(PIN_OPACITY) }
        }
    }
    addPin(cafePoint, cafeIcon)
    addPin(pizzeriaPoint, pizzaIcon)
}