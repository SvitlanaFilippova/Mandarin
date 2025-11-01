package com.mandarinkafe.mandarin.features.address.data

import com.mandarinkafe.mandarin.core.domain.models.DeliveryZone
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.data.dto.DeliveryZoneDto
import com.mandarinkafe.mandarin.features.address.data.dto.GeometryDto

/**
 * Извлекает число из названия зоны доставки.
 * Поддерживает форматы: "Зона 1", "Зона 10", "10"
 */
private fun extractZoneIdFromName(name: String): Int? {
    // Пробуем найти число в конце строки или после слова "Зона"
    val regexes = listOf(
        Regex("""[Зз]она\s+(\d+)"""),  // "Зона 1", "зона 10"
        Regex("""(\d+)""")              // "10" или просто число в строке
    )

    for (regex in regexes) {
        val match = regex.find(name)
        if (match != null) {
            return match.groupValues.lastOrNull()?.toIntOrNull()
        }
    }

    return null
}

fun DeliveryZoneDto.toDomain(
    deliveryPrice: Int,
    parentArea: List<GeoPoint>? = null,
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

    val zoneId = extractZoneIdFromName(name) ?: 0

    return DeliveryZone(
        id = zoneId,
        polygon = polygon,
        parentArea = parentArea,
        colorHex = colorHex.orEmpty(),
        freeDeliveryThreshold = freeDeliveryThreshold,
        deliveryPrice = deliveryPrice
    )
}
