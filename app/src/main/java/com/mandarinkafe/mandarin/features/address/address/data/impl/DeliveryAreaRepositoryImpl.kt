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
import kotlinx.coroutines.flow.first
import java.io.IOException

class DeliveryAreaRepositoryImpl(
    private val networkClient: GoogleDocsNetworkClient,
    private val menuCache: MenuCache
) : DeliveryAreaRepository {
    private val logTag = "DeliveryZone Debug"
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

            // Ждём, пока меню загрузится
            menuCache.visibleMenu.first { it is Resource.Success }

            // Извлекаем цены из deliveryCategory
            val pricesCategory = menuCache.deliveryItems.value?.meals
            val pricesMap = if (pricesCategory != null) {
                pricesCategory.mapNotNull { meal ->
                    val zoneId = extractZoneIdFromDeliveryName(meal.name)
                    if (zoneId != null) {
                        zoneId to meal.price
                    } else {
                        Log.d("DeliveryDebug", "Meal '${meal.name}' -> no zoneId extracted")
                        null
                    }
                }.toMap()
            } else {
                Log.d("DeliveryDebug", "No delivery category found")
                emptyMap()
            }

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
        val zonesMap = mutableMapOf<Int, List<GeoPoint>>()

        csv.lineSequence()
            .drop(1) // Пропускаем заголовок "WKT,название,описание"
            .forEach { line ->
                try {
                    // Разделяем строку на колонки (учитываем, что WKT может содержать запятые)
                    val columns = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())

                    if (columns.size >= 2) {
                        val wkt = columns[0].trim()
                        val name = columns[1].trim()

                        // Извлекаем ID из названия
                        val zoneId = extractZoneIdFromName(name) ?: run {
                            Log.w(logTag, "Cannot extract zone ID from name: $name")
                            return@forEach
                        }

                        // Парсим WKT в список точек
                        val points = parseWktToGeoPoints(wkt)
                        if (points.isNotEmpty()) {
                            zonesMap[zoneId] = points
                        } else {
                            Log.w(logTag, "No points parsed for zone $zoneId, WKT: $wkt")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(logTag, "Error parsing line: $line, error: ${e.message}")
                }
            }

        return zonesMap
    }

    private fun parseWktToGeoPoints(wkt: String): List<GeoPoint> {
        val points = mutableListOf<GeoPoint>()

        try {
            // Убираем лишние пробелы и приводим к верхнему регистру для удобства
            val cleanWkt = wkt.trim().uppercase()

            // Ищем основной полигон в WKT
            val polygonRegex = """POLYGON\s*\(\(([^)]+)\)\)""".toRegex()
            val polygonMatch = polygonRegex.find(cleanWkt)

            if (polygonMatch != null) {
                // Извлекаем координаты полигона
                val coordinates = polygonMatch.groupValues[1]
                parseCoordinatesString(coordinates, points)
            } else {
                // Пробуем найти GEOMETRYCOLLECTION
                val geometryCollectionRegex = """GEOMETRYCOLLECTION\s*\((.+)\)""".toRegex()
                val geometryMatch = geometryCollectionRegex.find(cleanWkt)

                if (geometryMatch != null) {
                    val geometryContent = geometryMatch.groupValues[1]
                    // Ищем все полигоны в коллекции
                    val polygons = geometryContent.split("(?=POLYGON)".toRegex())

                    polygons.forEach { polygonPart ->
                        val polyMatch = polygonRegex.find(polygonPart)
                        if (polyMatch != null) {
                            val coords = polyMatch.groupValues[1]
                            parseCoordinatesString(coords, points)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(logTag, "Error parsing WKT: $wkt, error: ${e.message}")
        }

        return points
    }

    private fun parseCoordinatesString(coordinates: String, points: MutableList<GeoPoint>) {
        // Разделяем пары координат
        val coordinatePairs = coordinates.split(",")

        coordinatePairs.forEach { pair ->
            val trimmedPair = pair.trim()
            if (trimmedPair.isNotBlank()) {
                // Разделяем longitude и latitude (в WKT порядок: долгота широта)
                val coords = trimmedPair.split("\\s+".toRegex())
                if (coords.size == 2) {
                    try {
                        val longitude = coords[0].toDouble()
                        val latitude = coords[1].toDouble()
                        points.add(GeoPoint(latitude, longitude))
                    } catch (e: NumberFormatException) {
                        Log.w(logTag, "Invalid coordinates: $trimmedPair")
                    }
                }
            }
        }
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

    private fun extractZoneIdFromDeliveryName(name: String): Int? {
        val regex = Regex("Доставка зона (\\d+)")
        val zoneId = regex.find(name)?.groupValues?.getOrNull(1)?.toIntOrNull()
        return zoneId
    }

    private fun extractZoneIdFromName(name: String): Int? {
        // Убираем кавычки если есть
        val cleanName = name.replace("\"", "").trim()

        // Пробуем разные форматы названий
        val regexes = listOf(
            Regex("""(\d+)"""), // Просто число
            Regex("""[Зз]она\s*(\d+)"""),
            Regex("""[Aa]rea\s*(\d+)"""),
            Regex("""[Zz]one\s*(\d+)""")
        )

        for (regex in regexes) {
            val match = regex.find(cleanName)
            if (match != null) {
                return match.groupValues[1].toIntOrNull()
            }
        }

        return null
    }
}
