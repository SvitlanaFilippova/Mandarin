package com.mandarinkafe.mandarin.features.address.address.data.impl

import com.mandarinkafe.mandarin.core.data.dto.CsvResponse
import com.mandarinkafe.mandarin.core.data.network.GoogleDocsNetworkClient
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.DeliveryZone
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.address.data.dto.ZoneMeta
import com.mandarinkafe.mandarin.features.address.address.domain.api.DeliveryAreaRepository
import io.github.aakira.napier.Napier
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.first
import java.io.IOException

class DeliveryAreaRepositoryImpl(
    private val networkClient: GoogleDocsNetworkClient,
    private val menuCache: MenuCache
) : DeliveryAreaRepository {
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
            menuCache.mainMenu.first { it is Resource.Success }

            // Извлекаем цены из deliveryCategory
            val pricesCategory = menuCache.deliveryItems.value?.meals
            val pricesMap = if (pricesCategory != null) {
                pricesCategory.mapNotNull { meal ->
                    val zoneId = extractZoneIdFromDeliveryName(meal.name)
                    if (zoneId != null) {
                        zoneId to meal.price
                    } else {
                        Napier.e("Meal '${meal.name}' -> no zoneId extracted")
                        null
                    }
                }.toMap()
            } else {
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
                Napier.w("Meta not found for zone id=$id — skipping")
                return@mapNotNull null
            }

            val parentArea = polygonsMap[id - 1]
            val deliveryPrice = pricesMap[id]
            if (deliveryPrice == null) {
                Napier.w("Price not found for zone id=$id — setting to 0")
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
            .drop(1) // Пропускаем заголовок
            .forEach { line ->
                val columns = splitCsvLine(line) ?: return@forEach
                val (wkt, name) = columns

                val zoneId = extractZoneIdFromName(name)
                if (zoneId == null) {
                    Napier.w("Cannot extract zone ID from name: $name")
                    return@forEach
                }

                val points = runCatching { parseWktToGeoPoints(wkt) }
                    .getOrElse {
                        Napier.e("Error parsing WKT for line: $line, error: ${it.message}")
                        emptyList()
                    }

                if (points.isEmpty()) {
                    Napier.w("No points parsed for zone $zoneId, WKT: $wkt")
                    return@forEach
                }

                zonesMap[zoneId] = points
            }

        return zonesMap
    }

    private fun splitCsvLine(line: String): Pair<String, String>? {
        return try {
            val columns = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())
            if (columns.size >= 2) {
                columns[0].trim() to columns[1].trim()
            } else {
                null
            }
        } catch (e: Exception) {
            Napier.e("Error splitting CSV line: $line, error: ${e.message}")
            null
        }
    }

    private fun parseWktToGeoPoints(wkt: String): List<GeoPoint> {
        val cleanWkt = wkt.trim().uppercase()

        parsePolygon(cleanWkt)?.let { return it }
        parseGeometryCollection(cleanWkt)?.let { return it }

        return emptyList()
    }

    private fun parsePolygon(wkt: String): List<GeoPoint>? {
        val polygonRegex = """POLYGON\s*\(\(([^)]+)\)\)""".toRegex()
        val match = polygonRegex.find(wkt) ?: return null
        return parseCoordinates(match.groupValues[1])
    }

    private fun parseGeometryCollection(wkt: String): List<GeoPoint>? {
        val geometryCollectionRegex = """GEOMETRYCOLLECTION\s*\((.+)\)""".toRegex()
        val match = geometryCollectionRegex.find(wkt) ?: return null

        val polygonRegex = """POLYGON\s*\(\(([^)]+)\)\)""".toRegex()
        return match.groupValues[1]
            .split("(?=POLYGON)".toRegex())
            .mapNotNull { polygonRegex.find(it)?.groupValues?.get(1) }
            .flatMap { parseCoordinates(it) }
    }

    private fun parseCoordinates(coordinates: String): List<GeoPoint> {
        return coordinates
            .split(",")
            .mapNotNull { pair ->
                val coords = pair.trim().split("\\s+".toRegex())
                if (coords.size != 2) return@mapNotNull null
                runCatching {
                    val lon = coords[0].toDouble()
                    val lat = coords[1].toDouble()
                    GeoPoint(lat, lon)
                }.getOrElse {
                    Napier.w("Invalid coordinates: $pair")
                    null
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
