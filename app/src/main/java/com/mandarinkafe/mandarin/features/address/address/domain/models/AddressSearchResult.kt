package com.mandarinkafe.mandarin.features.address.address.domain.models

import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.search.Address
import com.yandex.mapkit.search.ToponymObjectMetadata

data class AddressSearchResult(
    val point: GeoPoint?,
    val addressLineOne: String,
    val addressLineTwo: String?
) {
    val addressSingleLine: String
        get() = if (!addressLineTwo.isNullOrBlank()) {
            "$addressLineOne, $addressLineTwo"
        } else {
            addressLineOne
        }
}

fun GeoObject.toAddressSearchResult(): AddressSearchResult {
    val poiName = name?.takeIf { it.isNotBlank() }

    val toponymMetadata = metadataContainer.getItem(ToponymObjectMetadata::class.java)
    val address = toponymMetadata?.address
    val rawFullAddress = address?.formattedAddress ?: "—"

    val locality = address
        ?.components
        ?.firstOrNull { it.kinds.contains(Address.Component.Kind.LOCALITY) }
        ?.name
        .takeIf { !it.isNullOrBlank() }

    // Страна (первый компонент)
    val country = address
        ?.components
        ?.firstOrNull { it.kinds.contains(Address.Component.Kind.COUNTRY) }
        ?.name

    // Удалим страну (если есть) и дублирующийся poiName из formattedAddress
    var fullAddress = rawFullAddress

    if (!country.isNullOrBlank() && fullAddress.startsWith("$country, ")) {
        fullAddress = fullAddress.removePrefix("$country, ")
    }

    if (!poiName.isNullOrBlank()) {
        fullAddress = fullAddress.removeSuffix(", $poiName")
            .removeSuffix(poiName)
            .trimEnd(',', ' ')
    }

    val addressLineOne = poiName ?: locality.orEmpty()

    val addressLineTwo = if (locality == null || locality == poiName) {
        fullAddress
    } else {
        locality
    }

    return AddressSearchResult(
        point = geometry.firstOrNull()?.point?.toGeoPoint(),
        addressLineOne = addressLineOne,
        addressLineTwo = addressLineTwo
    )
}
