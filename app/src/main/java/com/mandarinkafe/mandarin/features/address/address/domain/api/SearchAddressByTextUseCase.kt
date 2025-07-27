package com.mandarinkafe.mandarin.features.address.address.domain.api

import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.address.domain.models.AddressSearchResult
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.StateFlow

interface SearchAddressByTextUseCase {
    suspend operator fun invoke(query: String, point: GeoPoint)
    fun observeSearchResults(): StateFlow<Resource<List<AddressSearchResult>>>
}