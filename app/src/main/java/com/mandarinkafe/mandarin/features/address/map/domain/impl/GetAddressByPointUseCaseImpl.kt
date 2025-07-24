package com.mandarinkafe.mandarin.features.address.map.domain.impl

import com.mandarinkafe.mandarin.features.address.map.domain.api.AddressRepository
import com.mandarinkafe.mandarin.features.address.map.domain.api.GetAddressByPointUseCase
import com.mandarinkafe.mandarin.features.address.map.domain.models.GeoPoint

class GetAddressByPointUseCaseImpl(private val addressRepository: AddressRepository) :
    GetAddressByPointUseCase {
    override fun observeAddress() = addressRepository.addressFlow

    override suspend fun invoke(point: GeoPoint) {
        return addressRepository.getAddressFromPoint(point)
    }
}