package com.mandarinkafe.mandarin.util.presentation.ui.components

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area1
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area10
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area11
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area12
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area2
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area3
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area4
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area5
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area6
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area7
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area8
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area9
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.mapview.MapView

@Composable
fun MapAreas(mapView: MapView) {
    addColoredArea(
        mapView,
        Area1,
        listOf(),
        Colors.FirstArea.toArgb()
    )
    addColoredArea(
        mapView,
        Area2,
        Area1,
        Colors.SecondArea.toArgb()
    )
    addColoredArea(
        mapView,
        Area3,
        Area2,
        Colors.ThirdArea.toArgb()
    )
    addColoredArea(
        mapView,
        Area4,
        Area3,
        Colors.FourthArea.toArgb()
    )
    addColoredArea(
        mapView,
        Area5,
        Area4,
        Colors.FifthArea.toArgb()
    )
    addColoredArea(
        mapView,
        Area6,
        Area5,
        Colors.SixthArea.toArgb()
    )
    addColoredArea(
        mapView,
        Area7,
        Area6,
        Colors.SeventhArea.toArgb()
    )
    addColoredArea(
        mapView,
        Area8,
        Area7,
        Colors.EighthArea.toArgb()
    )
    addColoredArea(
        mapView,
        Area9,
        Area8,
        Colors.NinthArea.toArgb()
    )
    addColoredArea(
        mapView,
        Area10,
        Area9,
        Colors.TenthArea.toArgb()
    )
    addColoredArea(
        mapView,
        Area11,
        Area10,
        Colors.EleventhArea.toArgb()
    )
    addColoredArea(
        mapView,
        Area12,
        Area11,
        Colors.TwelfthArea.toArgb()
    )
}

private fun addColoredArea(mapView: MapView, outer: List<Point>, hole: List<Point>, color: Int) {
    val mapObjects = mapView.mapWindow?.map?.mapObjects ?: return
    val polygon = if (!hole.isEmpty()) {
        Polygon(LinearRing(outer), listOf(LinearRing(hole)))
    } else {
        Polygon(LinearRing(outer), emptyList())
    }
    val polygonObject = mapObjects.addPolygon(polygon)
    polygonObject.apply {
        fillColor = color
        strokeColor = Color.TRANSPARENT
    }
}