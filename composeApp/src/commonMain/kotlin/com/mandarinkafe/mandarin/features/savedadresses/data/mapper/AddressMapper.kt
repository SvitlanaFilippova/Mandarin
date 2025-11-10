package com.mandarinkafe.mandarin.features.savedadresses.data.mapper

import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.AddressType
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.savedadresses.data.network.dto.AddressDto
import com.mandarinkafe.mandarin.features.savedadresses.data.network.dto.GeoPointDto
import io.github.aakira.napier.Napier

object AddressMapper {

    fun Address.toDto(): AddressDto {
        return AddressDto(
            id = id,
            point = point?.toDto(),
            streetAndBuilding = streetAndBuilding.takeIf { it.isNotBlank() },
            addressType = addressType?.name,
            apartmentNumber = apartmentNumber.takeIf { it.isNotBlank() },
            entrance = entrance.takeIf { it.isNotBlank() },
            floor = floor.takeIf { it.isNotBlank() },
            intercom = intercom.takeIf { it.isNotBlank() },
            comment = comment.takeIf { it.isNotBlank() },
        )
    }

    fun AddressDto.toDomain(): Address {
        return Address(
            id = id,
            point = point?.toDomain(),
            streetAndBuilding = streetAndBuilding ?: "",
            addressType = addressType?.let { parseAddressType(it) },
            apartmentNumber = apartmentNumber ?: "",
            entrance = entrance ?: "",
            floor = floor ?: "",
            intercom = intercom ?: "",
            comment = comment ?: "",
        )
    }

    fun GeoPoint.toDto(): GeoPointDto {
        return GeoPointDto(
            latitude = latitude,
            longitude = longitude,
        )
    }

    fun GeoPointDto.toDomain(): GeoPoint {
        return GeoPoint(
            latitude = latitude,
            longitude = longitude,
        )
    }

    private fun parseAddressType(type: String): AddressType? {
        return try {
            AddressType.valueOf(type)
        } catch (e: IllegalArgumentException) {
            Napier.e("AddressMapper, parseAddressType error: $e")
            null
        }
    }
}

