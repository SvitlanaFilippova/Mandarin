package com.mandarinkafe.mandarin.features.location.domain.impl

import com.mandarinkafe.mandarin.features.location.domain.api.AddressRepository
import com.mandarinkafe.mandarin.features.location.domain.api.GetAddressByPointUseCase
import com.mandarinkafe.mandarin.features.location.domain.models.GeoPoint

class GetAddressByPointUseCaseImpl(private val addressRepository: AddressRepository) :
    GetAddressByPointUseCase {
    override suspend fun invoke(point: GeoPoint): String? {
        return addressRepository.getAddressFromPoint(point)
    }
}