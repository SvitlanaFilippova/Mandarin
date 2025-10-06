package com.mandarinkafe.mandarin.features.address.address.data

import com.mandarinkafe.mandarin.core.domain.models.DeliveryZone
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.address.data.dto.DeliveryZoneDto

fun DeliveryZoneDto.toDomain(deliveryPrice: Int, parentArea: List<GeoPoint>? = null): DeliveryZone {
    // Берем первую фигуру и первую координатную последовательность
    val polygon = points
        .firstOrNull()
        ?.geometry
        ?.coordinates
        ?.firstOrNull()
        ?.map { coord ->
            GeoPoint(latitude = coord[1], longitude = coord[0])
        } ?: emptyList()

    return DeliveryZone(
        id = id,
        polygon = polygon,
        parentArea = parentArea,
        colorHex = colorHex.orEmpty(),
        freeDeliveryThreshold = freeDeliveryThreshold,
        deliveryPrice = deliveryPrice
    )
}
