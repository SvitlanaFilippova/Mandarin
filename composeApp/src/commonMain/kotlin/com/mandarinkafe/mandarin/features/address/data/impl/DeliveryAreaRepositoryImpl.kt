package com.mandarinkafe.mandarin.features.address.data.impl

import com.mandarinkafe.mandarin.core.data.network.ServerNetworkClient
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.DeliveryZone
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.data.dto.DeliveryZoneDto
import com.mandarinkafe.mandarin.features.address.data.dto.DeliveryZonesResponse
import com.mandarinkafe.mandarin.features.address.data.dto.GeometryDto
import com.mandarinkafe.mandarin.features.address.data.toDomain
import com.mandarinkafe.mandarin.features.address.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.util.Resource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first

class DeliveryAreaRepositoryImpl(
    private val networkClient: ServerNetworkClient,
    private val menuCache: MenuCache,
) : DeliveryAreaRepository {

    private var cachedZones: List<DeliveryZone>? = null

    override suspend fun getAllAreas(): Resource<List<DeliveryZone>> {
        cachedZones?.let {
            return Resource.Success(it)
        }

        return try {
            val response = networkClient.getDeliveryZones()

            val zonesDto = when (response.resultCode) {
                com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION -> {
                    return Resource.ErrorNoInternet()
                }

                com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS -> {
                    val zonesResponse = response as? DeliveryZonesResponse
                    if (zonesResponse == null) {
                        return Resource.ErrorOther("Неожиданный формат ответа от сервера")
                    }

                    val dto = zonesResponse.data

                    if (dto.isEmpty()) {
                        return Resource.ErrorEmptyData()
                    }

                    dto
                }

                else -> {
                    return Resource.ErrorOther("Ошибка сервера при получении зон доставки (код: ${response.resultCode})")
                }
            }

            menuCache.mainMenu.first { it is Resource.Success }

            val pricesMap = menuCache.deliveryItems.value?.meals?.mapNotNull { meal ->
                val zoneId = extractZoneIdFromDeliveryName(meal.name)
                zoneId?.let { it to meal.price }
            }?.toMap() ?: emptyMap()

            val zones = mapZonesToDomain(zonesDto, pricesMap)

            cachedZones = zones
            Resource.Success(zones)

        } catch (e: Exception) {
            Napier.e("${LOG_TAG}: ${ERROR_GETTING_ZONES}: ${e.message}", e)
            Resource.ErrorOther("${ERROR_GETTING_ZONES}: ${e.message}")
        }
    }

    private fun extractZoneIdFromDeliveryName(name: String): Int? {
        val regex = Regex(DELIVERY_ZONE_NAME_REGEX)
        return regex.find(name)?.groupValues?.getOrNull(REGEX_GROUP_INDEX)?.toIntOrNull()
    }

    private fun mapZonesToDomain(
        zonesDto: List<DeliveryZoneDto>,
        pricesMap: Map<Int, Int>,
    ): List<DeliveryZone> {
        // Сортируем по извлеченному ID из name
        val sortedDto = zonesDto.sortedBy { dto ->
            extractZoneIdFromName(dto.name) ?: DEFAULT_SORT_ID
        }

        return sortedDto.mapIndexed { index, dto ->
            if (dto.points.isEmpty()) {
                Napier.w("${LOG_TAG}: ${WARNING_NO_POINTS}: ${dto.name}")
                null
            } else {
                val zoneId = extractZoneIdFromName(dto.name)
                if (zoneId == null) {
                    Napier.w("${LOG_TAG}: ${WARNING_CANNOT_EXTRACT_ID}: ${dto.name}")
                }

                val parentArea = if (index > FIRST_INDEX) {
                    val prevGeometry = sortedDto[index - 1].points.firstOrNull()?.geometry

                    when (prevGeometry) {
                        is GeometryDto.PolygonGeometry -> {
                            prevGeometry.coordinates.firstOrNull()?.mapNotNull { coord ->
                                if (coord.size >= MIN_COORD_SIZE) {
                                    GeoPoint(
                                        coord[LATITUDE_INDEX],
                                        coord[LONGITUDE_INDEX]
                                    )
                                } else null
                            }
                        }

                        is GeometryDto.MultiPolygonGeometry -> {
                            prevGeometry.coordinates.firstOrNull()?.firstOrNull()
                                ?.mapNotNull { coord ->
                                    if (coord.size >= MIN_COORD_SIZE) {
                                        GeoPoint(
                                            coord[LATITUDE_INDEX],
                                            coord[LONGITUDE_INDEX]
                                        )
                                    } else null
                                }
                        }

                        else -> null
                    }
                } else null

                dto.toDomain(
                    deliveryPrice = zoneId?.let { pricesMap[it] } ?: DEFAULT_DELIVERY_PRICE,
                    parentArea = parentArea
                )
            }
        }.filterNotNull()
    }

    private fun extractZoneIdFromName(name: String): Int? {
        // Пробуем найти число в конце строки или после слова "Зона"
        val regexes = listOf(
            Regex(ZONE_NAME_WITH_WORD_REGEX), // "Зона 1", "зона 10"
            Regex(NUMBER_ONLY_REGEX) // "10" или просто число в строке
        )

        for (regex in regexes) {
            val match = regex.find(name)
            if (match != null) {
                return match.groupValues.lastOrNull()?.toIntOrNull()
            }
        }

        return null
    }

    private companion object {
        const val LOG_TAG = "DeliveryZone Debug"
        const val ERROR_GETTING_ZONES = "Ошибка при получении зон доставки"
        const val WARNING_NO_POINTS = "Zone has no points, skipping"
        const val WARNING_CANNOT_EXTRACT_ID = "Cannot extract zone ID from name"

        const val DELIVERY_ZONE_NAME_REGEX = "Доставка зона (\\d+)"
        const val ZONE_NAME_WITH_WORD_REGEX = """[Зз]она\s+(\d+)"""
        const val NUMBER_ONLY_REGEX = """(\d+)"""

        const val REGEX_GROUP_INDEX = 1

        const val DEFAULT_SORT_ID = Int.MAX_VALUE
        const val DEFAULT_DELIVERY_PRICE = 0

        const val FIRST_INDEX = 0
        const val MIN_COORD_SIZE = 2
        const val LATITUDE_INDEX = 1
        const val LONGITUDE_INDEX = 0
    }
}
