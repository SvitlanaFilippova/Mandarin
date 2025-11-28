package com.mandarinkafe.mandarin.features.address.domain.models

import com.mandarinkafe.mandarin.core.domain.models.GeoPoint

data class AddressSearchResult(
    val point: GeoPoint?,
    val addressLineOne: String,
    val addressLineTwo: String?,
) {
    val addressSingleLine: String
        get() = if (!addressLineTwo.isNullOrBlank()) {
            "$addressLineOne, $addressLineTwo"
        } else {
            addressLineOne
        }
}
