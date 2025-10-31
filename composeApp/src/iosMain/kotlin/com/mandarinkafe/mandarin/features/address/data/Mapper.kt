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
        // Название объекта (POI)
        val poiName = try {
            name?.takeIf { it.isNotBlank() }
        } catch (_: Throwable) {
            null
        }

        // Описание (обычно "Город, область, страна")
        val rawDescription = try {
            descriptionText?.takeIf { it.isNotBlank() }
        } catch (_: Throwable) {
            null
        }

        var fullAddress = rawDescription ?: poiName ?: ""

        // Если обе строки пусты — пробуем вытащить что-нибудь из attributionMap
        if (fullAddress.isBlank()) {
            try {
                val firstValue = attributionMap.values.firstOrNull()?.toString()
                if (!firstValue.isNullOrBlank()) fullAddress = firstValue
            } catch (_: Throwable) { /* ignore */
            }
        }

        // Попробуем достать город или район из descriptionText — обычно он ближе к началу
        val locality: String? = run {
            if (rawDescription.isNullOrBlank()) return@run null
            val parts = rawDescription.split(',')
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            // выбираем первый элемент, если в нём нет слова "область" или "край"
            parts.firstOrNull { !it.contains("область", true) && !it.contains("край", true) }
                ?: parts.getOrNull(parts.size - 2)
        }

        // Очищаем fullAddress от дублирования
        fullAddress = fullAddress
            .removePrefix("${poiName ?: ""}, ").trim()
            .removePrefix("${locality ?: ""}, ").trim()
            .removeSuffix(", ${poiName ?: ""}").trim()
            .removeSuffix(", ${locality ?: ""}").trim()
            .trim(',', ' ', '\u00A0')

        // Собираем две строки по заданной логике
        val addressLineOne = poiName ?: locality.orEmpty()
        val addressLineTwo = if (locality == null || locality == poiName) {
            fullAddress.ifBlank { null }
        } else {
            locality
        }

        // Извлекаем точку
        val yPoint = extractFirstPointFromGeometryList(
            try {
                geometry
            } catch (_: Throwable) {
                null
            }
        )
        val point = yPoint?.toGeoPoint()

        return AddressSearchResult(
            point = point,
            addressLineOne = addressLineOne,
            addressLineTwo = addressLineTwo
        )
    }

    private fun extractFirstPointFromGeometryList(geometryList: Any?): YMKPoint? {
        val list = (geometryList as? List<*>) ?: return null
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


