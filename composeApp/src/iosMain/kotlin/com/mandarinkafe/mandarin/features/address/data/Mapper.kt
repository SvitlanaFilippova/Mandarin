@file:OptIn(ExperimentalForeignApi::class)
package com.mandarinkafe.mandarin.features.address.data

import YandexMapKit.*
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
        // POI name (название объекта)
        val poiName = try {
            name?.takeIf { it.isNotBlank() }
        } catch (_: Throwable) {
            null
        }

        // Попытка получить "готовое" описание / адрес из descriptionText
        val rawDescription = try {
            descriptionText?.takeIf { it.isNotBlank() }
        } catch (_: Throwable) {
            null
        }

        // fullAddress — приоритет: descriptionText -> name -> ""
        var fullAddress = rawDescription ?: poiName ?: ""

        // Эвристика: если descriptionText пустой, возможно есть текст в attributionMap или в aref — но это опционально.
        if (fullAddress.isBlank()) {
            try {
                val attr = attributionMap
                if (attr.isNotEmpty()) {
                    // берем первый value как запасной адрес
                    val firstValue = attr.values.firstOrNull()?.toString()
                    if (!firstValue.isNullOrBlank()) fullAddress = firstValue
                }
            } catch (_: Throwable) { /* ignore */ }
        }

        // Выделяем locality из fullAddress простыми правилами (разделитель ",")
        val locality: String? = run {
            if (fullAddress.isBlank()) return@run null
            val parts = fullAddress.split(',')
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            when {
                parts.size >= 3 -> parts[parts.size - 2] // часто: Country, City, Street -> берём City
                parts.size == 2 -> parts[0] // City, Street -> City
                parts.size == 1 -> null
                else -> parts.firstOrNull()
            }?.takeIf { it.isNotBlank() }
        }

        // Очистка fullAddress: убираем локалити или poiName, если они дублируются
        if (!locality.isNullOrBlank() && fullAddress.startsWith("$locality, ")) {
            fullAddress = fullAddress.removePrefix("$locality, ").trimStart()
        }

        if (!poiName.isNullOrBlank()) {
            fullAddress = fullAddress
                .removePrefix("$poiName, ")
                .removeSuffix(", $poiName")
                .removeSuffix(poiName)
                .trim(',', ' ', '\u00A0')
        }

        // Сохраняем требуемую логику выбора строк
        val addressLineOne = poiName ?: locality.orEmpty()
        val addressLineTwo = if (locality == null || locality == poiName) {
            fullAddress.ifBlank { null }
        } else {
            locality
        }

        // Извлекаем точку из geometry (если есть)
        val yPoint = extractFirstPointFromGeometryList(try { geometry } catch (_: Throwable) { null })
        val point = yPoint?.toGeoPoint()

        return AddressSearchResult(
            point = point,
            addressLineOne = addressLineOne,
            addressLineTwo = addressLineTwo
        )
    }

    private fun extractFirstPointFromGeometryList(geometryList: Any?): YMKPoint? {
        // geometryList обычно — kotlin.collections.List<*>
        val list = (geometryList as? List<*>) ?: return null

        // Первый элемент часто YMKGeometry
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


