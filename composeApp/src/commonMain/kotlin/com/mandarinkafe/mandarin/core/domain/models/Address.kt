package com.mandarinkafe.mandarin.core.domain.models

import androidx.compose.runtime.Stable
import com.mandarinkafe.mandarin.util.generateUuid
import kotlinx.serialization.Serializable

@Serializable
@Stable
data class Address(
    val id: String = generateUuid(),
    val point: GeoPoint? = null,
    val streetAndBuilding: String = "",
    val addressType: AddressType? = null,
    val apartmentNumber: String = "",
    val entrance: String = "",
    val floor: String = "",
    val intercom: String = "",
    val comment: String = "",
) {
    val noNeedAddressDetails: Boolean
        get() = addressType == AddressType.PRIVATE_HOUSE || addressType == AddressType.OTHER
}

fun Address.getDetailsString(): String {
    val parts = listOfNotNull(
        apartmentNumber.takeIf { it.isNotBlank() }?.let { "кв. $it" },
        entrance.takeIf { it.isNotBlank() }?.let { "подъезд $it" },
        intercom.takeIf { it.isNotBlank() }?.let { "домофон $it" },
        floor.takeIf { it.isNotBlank() }?.let { "этаж $it" },
        comment.takeIf { it.isNotBlank() },
    )
    return parts.joinToString(separator = ", ")

}