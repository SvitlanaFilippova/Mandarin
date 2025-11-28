@file:OptIn(ExperimentalForeignApi::class)

package com.mandarinkafe.mandarin.util.presentation.ui.components.map

import YandexMapKit.YMKLinearRing
import YandexMapKit.YMKMapView
import YandexMapKit.YMKPoint
import YandexMapKit.YMKPolygon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.presentation.ui.models.UiDeliveryArea
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIColor

@Composable
fun DeliveryAreasOnMap(
    mapView: YMKMapView,
    deliveryAreas: List<UiDeliveryArea>,
) {
    // Создаем ключ для отслеживания изменений зон доставки
    val areasKey = remember(deliveryAreas) {
        deliveryAreas.joinToString { "${it.id}-${it.polygon.size}-${it.color.toArgb()}" }
    }

    LaunchedEffect(areasKey) {
        // Очищаем старые полигоны
        clearDeliveryAreas(mapView)

        // Добавляем новые полигоны с учётом вложенности
        deliveryAreas.forEachIndexed { index, area ->
            val parent = deliveryAreas.getOrNull(index - 1)
            addColoredArea(
                mapView = mapView,
                outer = area.polygon,
                hole = parent?.polygon,
                color = area.color
            )
        }
    }
}

private fun clearDeliveryAreas(mapView: YMKMapView) {
    val mapObjects = mapView.mapWindow?.map?.mapObjects ?: return
    mapObjects.clear()
}

private fun addColoredArea(
    mapView: YMKMapView,
    outer: List<GeoPoint>,
    hole: List<GeoPoint>?,
    color: Color,
) {
    // Конвертация цвета Compose -> UIColor
    val fillUIColor = color.copy(alpha = 0.3f).toUIColor()
    val strokeUIColor = color.copy(alpha = 0.1f).toUIColor()

    // Получаем коллекцию объектов карты
    val mapObjects = mapView.mapWindow?.map?.mapObjects ?: return

    // Преобразуем в YMKPoint
    val outerPoints = outer.map { YMKPoint.pointWithLatitude(it.latitude, it.longitude) }
    val holePoints =
        hole?.map { YMKPoint.pointWithLatitude(it.latitude, it.longitude) } ?: emptyList()
    val outerRing = YMKLinearRing.linearRingWithPoints(outerPoints)
    val innerRings =
        if (holePoints.isNotEmpty()) listOf(YMKLinearRing.linearRingWithPoints(holePoints)) else emptyList()
    val polygon = YMKPolygon.polygonWithOuterRing(outerRing, innerRings)

    val polygonObject = mapObjects.addPolygonWithPolygon(polygon)

    // Задаём цвета (UIColor)
    polygonObject.fillColor = fillUIColor
    polygonObject.strokeColor = strokeUIColor

    return
}

/**
 * Утилита для конвертации Compose Color -> UIColor
 */
private fun Color.toUIColor(): UIColor {
    return UIColor.colorWithRed(
        red = red.toDouble(),
        green = green.toDouble(),
        blue = blue.toDouble(),
        alpha = alpha.toDouble()
    )
}