package com.mandarinkafe.mandarin.features.address.map.presentation.viewmodel

import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.map.VisibleRegion

fun visibleRegionToBoundingBox(region: VisibleRegion): Geometry {
    val points = listOf(
        region.topLeft,
        region.topRight,
        region.bottomLeft,
        region.bottomRight
    )

    val minLat = points.minOf { it.latitude }
    val maxLat = points.maxOf { it.latitude }
    val minLon = points.minOf { it.longitude }
    val maxLon = points.maxOf { it.longitude }

    val boundingBox = BoundingBox(
        com.yandex.mapkit.geometry.Point(minLat, minLon),
        com.yandex.mapkit.geometry.Point(maxLat, maxLon)
    )

    return Geometry.fromBoundingBox(boundingBox)
}