package com.mandarinkafe.mandarin.features.address.address.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.network.ServerNetworkClient
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.DeliveryZone
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.address.data.dto.DeliveryZoneDto
import com.mandarinkafe.mandarin.features.address.address.data.dto.DeliveryZonesResponse
import com.mandarinkafe.mandarin.features.address.address.data.dto.GeometryDto
import com.mandarinkafe.mandarin.features.address.address.data.toDomain
import com.mandarinkafe.mandarin.features.address.address.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.first

class DeliveryAreaRepositoryImpl(
    private val networkClient: ServerNetworkClient,
    private val menuCache: MenuCache
) : DeliveryAreaRepository {

    private val logTag = "DeliveryZone Debug"
    private var cachedZones: List<DeliveryZone>? = null

    override suspend fun getAllAreas(): Resource<List<DeliveryZone>> {
        cachedZones?.let { return Resource.Success(it) }

        return try {
            val response = networkClient.getDeliveryZones()
            val zonesDto = (response as DeliveryZonesResponse).data
            if (zonesDto.isEmpty()) return Resource.ErrorEmptyData()

            menuCache.mainMenu.first { it is Resource.Success }

            val pricesMap = menuCache.deliveryItems.value?.meals?.mapNotNull { meal ->
                val zoneId = extractZoneIdFromDeliveryName(meal.name)
                zoneId?.let { it to meal.price }
            }?.toMap() ?: emptyMap()

            val zones = mapZonesToDomain(zonesDto, pricesMap)

            cachedZones = zones
            Resource.Success(zones)

        } catch (e: Exception) {
            Log.e(logTag, "Ошибка при получении зон доставки: ${e.message}", e)
            Resource.ErrorOther("Ошибка при получении зон доставки: ${e.message}")
        }
    }

    private fun extractZoneIdFromDeliveryName(name: String): Int? {
        val regex = Regex("Доставка зона (\\d+)")
        return regex.find(name)?.groupValues?.getOrNull(1)?.toIntOrNull()
    }

    private fun mapZonesToDomain(
        zonesDto: List<DeliveryZoneDto>,
        pricesMap: Map<Int, Int>
    ): List<DeliveryZone> {
        val sortedDto = zonesDto.sortedBy { it.id }

        return sortedDto.mapIndexed { index, dto ->
            if (dto.points.isEmpty()) {
                Log.w("DeliveryZone Debug", "Zone ${dto.id} has no points, skipping")
                null
            } else {
                val parentArea = if (index > 0) {
                    val prevGeometry = sortedDto[index - 1].points.firstOrNull()?.geometry

                    when (prevGeometry) {
                        is GeometryDto.PolygonGeometry -> {
                            prevGeometry.coordinates.firstOrNull()?.mapNotNull { coord ->
                                if (coord.size >= 2) GeoPoint(coord[1], coord[0]) else null
                            }
                        }
                        is GeometryDto.MultiPolygonGeometry -> {
                            prevGeometry.coordinates.firstOrNull()?.firstOrNull()?.mapNotNull { coord ->
                                if (coord.size >= 2) GeoPoint(coord[1], coord[0]) else null
                            }
                        }
                        else -> null
                    }
                } else null

                dto.toDomain(
                    deliveryPrice = pricesMap[dto.id] ?: 0,
                    parentArea = parentArea
                )
            }
        }.filterNotNull()
    }

}

