package com.mandarinkafe.mandarin.features.address.address.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.address.domain.api.AddressRepository
import com.mandarinkafe.mandarin.features.address.address.domain.api.AddressSearchInteractor

class AddressSearchInteractorImpl(private val repository: AddressRepository) :
    AddressSearchInteractor {
    override fun observeAddress() = repository.addressStringFlow

    override fun observeSearchResults() = repository.addressListFlow

    override suspend fun searchAddressByText(
        query: String,
        point: GeoPoint
    ) {
        repository.searchAddressByString(query, point)
    }

    override suspend fun getAddressByPoint(point: GeoPoint) {
        repository.getAddressFromPoint(point)
    }
}