package com.mandarinkafe.mandarin.features.address.address.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.address.domain.api.AddressRepository
import com.mandarinkafe.mandarin.features.address.address.domain.api.SearchAddressByTextUseCase

class SearchAddressByTextUseCaseImpl(private val addressRepository: AddressRepository) :
    SearchAddressByTextUseCase {
    override suspend fun invoke(
        query: String,
        point: GeoPoint
    ) {
        addressRepository.searchAddressByString(query, point)
    }

    override fun observeSearchResults() = addressRepository.addressListFlow
}