package com.mandarinkafe.mandarin.features.address.address.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.dto.CsvResponse
import com.mandarinkafe.mandarin.core.data.network.GoogleDocsNetworkClient
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.DeliveryZone
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.address.data.dto.ZoneMeta
import com.mandarinkafe.mandarin.features.address.address.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.util.Resource
import java.io.IOException

class DeliveryAreaRepositoryImpl(
    private val networkClient: GoogleDocsNetworkClient,
    private val menuCache: MenuCache
) : DeliveryAreaRepository {
    private val logTag = "DeliveryZone Error"
    private var cachedZones: List<DeliveryZone>? = null

    override suspend fun getAllAreas(): Resource<List<DeliveryZone>> {
        // Возвращаем из кеша, если уже загружено
        cachedZones?.let {
            return Resource.Success(it)
        }

        return try {
            val polygonResult = networkClient.getDeliveryZonesPoints()
            val metaResult = networkClient.getDeliveryZonesMetaData()

            val polygonCsv = (polygonResult as? CsvResponse)?.csv.orEmpty()
            val metaCsv = (metaResult as? CsvResponse)?.csv.orEmpty()

            val polygonsMap = parsePolygonCsv(polygonCsv)
            val metaMap = parseMetaCsv(metaCsv)
            // Извлекаем цены из deliveryCategory
            val pricesCategory = menuCache.deliveryItems.value?.meals
            val pricesMap = pricesCategory
                ?.mapNotNull { meal ->
                    val zoneId = extractZoneIdFromName(meal.name)
                    if (zoneId != null) zoneId to meal.price else null
                }
                ?.toMap()
                .orEmpty()

            val zones = buildDeliveryZones(polygonsMap, metaMap, pricesMap)
            cachedZones = zones
            return Resource.Success(zones)
        } catch (e: IOException) {
            Resource.ErrorOther("Ошибка сети при загрузке зон доставки, $e")
        } catch (e: Exception) {
            Resource.ErrorOther("Неизвестная ошибка при получении зон доставки, $e")
        }
    }

    private fun buildDeliveryZones(
        polygonsMap: Map<Int, List<GeoPoint>>,
        metaMap: Map<Int, ZoneMeta>,
        pricesMap: Map<Int, Int>
    ): List<DeliveryZone> {
        return polygonsMap.mapNotNull { (id, polygon) ->
            val meta = metaMap[id]
            if (meta == null) {
                Log.w(logTag, "Meta not found for zone id=$id — skipping")
                return@mapNotNull null
            }

            val parentArea = polygonsMap[id - 1]
            if (parentArea == null) {
                Log.d(logTag, "Parent area not found for zone id=$id (parent=${id - 1})")
            }

            val deliveryPrice = pricesMap[id]
            if (deliveryPrice == null) {
                Log.w(logTag, "Price not found for zone id=$id — setting to 0")
            }

            DeliveryZone(
                id = id,
                polygon = polygon,
                parentArea = parentArea,
                colorHex = meta.colorHex,
                freeDeliveryThreshold = meta.freeDeliveryThreshold,
                deliveryPrice = deliveryPrice ?: 0
            )
        }.sortedBy { it.id }
    }

    private fun parsePolygonCsv(csv: String): Map<Int, List<GeoPoint>> {
        return csv
            .lineSequence()
            .drop(1) // Пропускаем заголовок
            .mapNotNull { line ->
                val parts = line.split(",")
                val id = parts.getOrNull(0)?.toIntOrNull()
                val lat = parts.getOrNull(1)?.toDoubleOrNull()
                val lon = parts.getOrNull(2)?.toDoubleOrNull()
                if (id != null && lat != null && lon != null) {
                    id to GeoPoint(lat, lon)
                } else {
                    null
                }
            }
            .groupBy({ it.first }, { it.second })
    }

    private fun parseMetaCsv(csv: String): Map<Int, ZoneMeta> {
        return csv
            .lineSequence()
            .drop(1)
            .mapNotNull { line ->
                val parts = line.split(",")
                val id = parts.getOrNull(0)?.toIntOrNull()
                val threshold = parts.getOrNull(1)?.toIntOrNull()
                val colorHex = parts.getOrNull(2)
                if (id != null && threshold != null && colorHex != null) {
                    id to ZoneMeta(id, threshold, colorHex)
                } else {
                    null
                }
            }
            .toMap()
    }

    private fun extractZoneIdFromName(name: String): Int? {
        val regex = Regex("Доставка зона (\\d+)")
        return regex.find(name)?.groupValues?.getOrNull(1)?.toIntOrNull()
    }
}
