package com.mandarinkafe.mandarin.features.address.address.domain.api

import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.address.domain.models.AddressSearchResult
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    val addressStringFlow: Flow<Resource<AddressSearchResult>>
    val addressListFlow: Flow<Resource<List<AddressSearchResult>>>
    suspend fun getAddressFromPoint(point: GeoPoint)
    suspend fun searchAddressByString(query: String, point: GeoPoint)
}