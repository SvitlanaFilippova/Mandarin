package com.mandarinkafe.mandarin.features.location.domain.api

import com.mandarinkafe.mandarin.features.location.domain.models.GeoPoint

interface AddressRepository {
    suspend fun getAddressFromPoint(point: GeoPoint): String?
    suspend fun searchAddressByString(query: String, point: GeoPoint): String?
}