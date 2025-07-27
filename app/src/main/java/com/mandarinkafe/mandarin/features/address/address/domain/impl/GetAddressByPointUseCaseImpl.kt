package com.mandarinkafe.mandarin.features.address.address.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.address.domain.api.AddressRepository
import com.mandarinkafe.mandarin.features.address.address.domain.api.GetAddressByPointUseCase

class GetAddressByPointUseCaseImpl(private val addressRepository: AddressRepository) :
    GetAddressByPointUseCase {
    override fun observeAddress() = addressRepository.addressStringFlow

    override suspend fun invoke(point: GeoPoint) {
        addressRepository.getAddressFromPoint(point)
    }
}