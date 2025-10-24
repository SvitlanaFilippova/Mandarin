package com.mandarinkafe.mandarin.util.presentation.ui.components.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.presentation.ui.models.UiDeliveryArea
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.mapview.MapView

@Composable
fun DeliveryAreasOnMap(
    mapView: MapView,
    deliveryAreas: List<UiDeliveryArea>
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

private fun clearDeliveryAreas(mapView: MapView) {
    val mapObjects = mapView.mapWindow?.map?.mapObjects ?: return
    mapObjects.clear()
}

private fun addColoredArea(
    mapView: MapView,
    outer: List<GeoPoint>,
    hole: List<GeoPoint>?,
    color: Color
) {
    val colorArea = color.copy(alpha = 0.3f).toArgb()
    val colorStroke = color.copy(alpha = 0.1f).toArgb()

    val mapObjects = mapView.mapWindow?.map?.mapObjects ?: return
    val outerPoints = outer.map { Point(it.latitude, it.longitude) }
    val holePoints = hole?.map { Point(it.latitude, it.longitude) } ?: emptyList()

    val polygon = if (holePoints.isNotEmpty()) {
        Polygon(LinearRing(outerPoints), listOf(LinearRing(holePoints)))
    } else {
        Polygon(LinearRing(outerPoints), emptyList())
    }

    val polygonObject = mapObjects.addPolygon(polygon)
    polygonObject.fillColor = colorArea
    polygonObject.strokeColor = colorStroke
}