package com.mandarinkafe.mandarin.features.address.address.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.DeliveryArea
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Colors.deliveryAreaColorMap
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.models.UiDeliveryArea
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

fun DeliveryArea.toUi(): UiDeliveryArea {
    return UiDeliveryArea(
        id = id,
        polygon = polygon,
        parentArea = parentArea,
        deliveryPrice = deliveryPrice,
        freeDeliveryThreshold = freeDeliveryThreshold,
        color = deliveryAreaColorMap[id] ?: Colors.LightGrey
    )
}
