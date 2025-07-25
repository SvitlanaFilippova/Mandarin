package com.mandarinkafe.mandarin.features.address.map.presentation.ui.components

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.map.presentation.ui.models.UiDeliveryArea
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.mapview.MapView

@Composable
fun DeliveryAreasOnMap(
    mapView: MapView,
    deliveryAreas: List<UiDeliveryArea>
) {
    // Прорисовка с учётом вложенности
    deliveryAreas.forEachIndexed { index, area ->
        val parent = deliveryAreas.getOrNull(index - 1)
        addColoredArea(
            mapView = mapView,
            outer = area.polygon,
            hole = parent?.polygon,
            color = area.color.toArgb()
        )
    }
}

private fun addColoredArea(
    mapView: MapView,
    outer: List<GeoPoint>,
    hole: List<GeoPoint>?,
    color: Int
) {
    val mapObjects = mapView.mapWindow?.map?.mapObjects ?: return
    val outerPoints = outer.map { Point(it.latitude, it.longitude) }
    val holePoints = hole?.map { Point(it.latitude, it.longitude) } ?: emptyList()

    val polygon = if (holePoints.isNotEmpty()) {
        Polygon(LinearRing(outerPoints), listOf(LinearRing(holePoints)))
    } else {
        Polygon(LinearRing(outerPoints), emptyList())
    }

    val polygonObject = mapObjects.addPolygon(polygon)
    polygonObject.fillColor = color
    polygonObject.strokeColor = Color.TRANSPARENT
}