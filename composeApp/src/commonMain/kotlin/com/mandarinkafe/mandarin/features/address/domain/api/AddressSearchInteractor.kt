package com.mandarinkafe.mandarin.features.address.domain.api

import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.domain.models.AddressSearchResult
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface AddressSearchInteractor {
    fun observeAddress(): Flow<Resource<AddressSearchResult>>
    fun observeSearchResults(): Flow<Resource<List<AddressSearchResult>>>
    suspend fun searchAddressByText(query: String, point: GeoPoint)
    suspend fun getAddressByPoint(point: GeoPoint)
}

