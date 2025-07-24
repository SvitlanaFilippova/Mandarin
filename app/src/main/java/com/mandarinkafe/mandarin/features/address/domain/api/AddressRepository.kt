package com.mandarinkafe.mandarin.features.address.domain.api

import com.mandarinkafe.mandarin.features.address.domain.models.GeoPoint
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.StateFlow

interface AddressRepository {
    val addressFlow: StateFlow<Resource<String>>
    suspend fun getAddressFromPoint(point: GeoPoint)
    suspend fun searchAddressByString(query: String, point: GeoPoint): String?
}