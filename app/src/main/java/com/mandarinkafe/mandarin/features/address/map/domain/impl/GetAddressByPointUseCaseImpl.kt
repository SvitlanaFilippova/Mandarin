package com.mandarinkafe.mandarin.features.address.map.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.map.domain.api.AddressRepository
import com.mandarinkafe.mandarin.features.address.map.domain.api.GetAddressByPointUseCase

class GetAddressByPointUseCaseImpl(private val addressRepository: AddressRepository) :
    GetAddressByPointUseCase {
    override fun observeAddress() = addressRepository.addressFlow

    override suspend fun invoke(point: GeoPoint) {
        return addressRepository.getAddressFromPoint(point)
    }
}