package com.mandarinkafe.mandarin.features.address.address.data

import com.mandarinkafe.mandarin.core.domain.models.DeliveryZone
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.address.data.dto.DeliveryZoneDto
import com.mandarinkafe.mandarin.features.address.address.data.dto.GeometryDto

fun DeliveryZoneDto.toDomain(
    deliveryPrice: Int,
    parentArea: List<GeoPoint>? = null
): DeliveryZone {
    val geometry = points.firstOrNull()?.geometry

    val polygon: List<GeoPoint> = when (geometry) {
        is GeometryDto.PolygonGeometry -> {
            // Берем первую последовательность координат (наружный контур)
            geometry.coordinates.firstOrNull()?.mapNotNull { coord ->
                if (coord.size >= 2) GeoPoint(latitude = coord[1], longitude = coord[0]) else null
            }.orEmpty()
        }

        is GeometryDto.MultiPolygonGeometry -> {
            // Берем первый полигон → первую последовательность координат
            geometry.coordinates.firstOrNull()?.firstOrNull()?.mapNotNull { coord ->
                if (coord.size >= 2) GeoPoint(latitude = coord[1], longitude = coord[0]) else null
            }.orEmpty()
        }

        else -> emptyList()
    }

    return DeliveryZone(
        id = id,
        polygon = polygon,
        parentArea = parentArea,
        colorHex = colorHex.orEmpty(),
        freeDeliveryThreshold = freeDeliveryThreshold,
        deliveryPrice = deliveryPrice
    )
}
