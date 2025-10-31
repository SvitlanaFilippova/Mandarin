@file:OptIn(ExperimentalForeignApi::class)

package com.mandarinkafe.mandarin.features.address.data

import YandexMapKit.YMKGeoObject
import YandexMapKit.YMKGeometry
import YandexMapKit.YMKPoint
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.domain.models.AddressSearchResult
import kotlinx.cinterop.ExperimentalForeignApi

object Mapper {
    fun GeoPoint.toYandexPoint(): YMKPoint =
        YMKPoint.pointWithLatitude(latitude, longitude)

    fun YMKPoint.toGeoPoint(): GeoPoint {
        return GeoPoint(latitude, longitude)
    }

    fun YMKGeoObject.toAddressSearchResult(): AddressSearchResult {
        val poiName = safeValue { name?.takeIf { it.isNotBlank() } }
        val rawDescription = safeValue { descriptionText?.takeIf { it.isNotBlank() } }

        var fullAddress = rawDescription ?: poiName ?: ""

        if (fullAddress.isBlank()) {
            fullAddress = extractFallbackFromAttributionMap()
        }

        val locality = extractLocality(rawDescription)

        fullAddress = cleanFullAddress(fullAddress, poiName, locality)

        val addressLineOne = poiName ?: locality.orEmpty()
        val addressLineTwo = if (locality == null || locality == poiName) {
            fullAddress.ifBlank { null }
        } else {
            locality
        }

        val point = extractFirstPointFromGeometryList(safeValue { geometry })?.toGeoPoint()

        return AddressSearchResult(
            point = point,
            addressLineOne = addressLineOne,
            addressLineTwo = addressLineTwo
        )
    }

    private fun extractFirstPointFromGeometryList(geometryList: Any?): YMKPoint? {
        val list = geometryList as? List<*> ?: return null
        val firstGeom = list.firstOrNull() ?: return null
        return try {
            // Попробуем привести и получить point() (метод) или point property
            val geom = firstGeom as? YMKGeometry
            geom?.point() ?: try {
                // попробовать как свойство
                val p = (firstGeom as? YMKGeometry)?.point
                p
            } catch (_: Throwable) {
                null
            }
        } catch (_: Throwable) {
            null
        }
    }
}

private inline fun <T> safeValue(block: () -> T): T? =
    try {
        block()
    } catch (_: Throwable) {
        null
    }

private fun YMKGeoObject.extractFallbackFromAttributionMap(): String {
    return attributionMap.values.firstOrNull()?.toString().takeIf { !it.isNullOrBlank() }.orEmpty()
}

private fun extractLocality(rawDescription: String?): String? {
    if (rawDescription.isNullOrBlank()) return null

    val parts = rawDescription.split(',')
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    return parts.firstOrNull {
        !it.contains("область", ignoreCase = true) &&
                !it.contains("край", ignoreCase = true)
    } ?: parts.getOrNull(parts.size - 2)
}

private fun cleanFullAddress(full: String, poi: String?, locality: String?): String =
    full.removePrefix("${poi ?: ""}, ").trim()
        .removePrefix("${locality ?: ""}, ").trim()
        .removeSuffix(", ${poi ?: ""}").trim()
        .removeSuffix(", ${locality ?: ""}").trim()
        .trim(',', ' ', '\u00A0')


