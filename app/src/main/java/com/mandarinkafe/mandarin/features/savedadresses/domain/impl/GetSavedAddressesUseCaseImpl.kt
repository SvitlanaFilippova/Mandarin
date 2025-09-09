package com.mandarinkafe.mandarin.features.savedadresses.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.GetSavedAddressesUseCase
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.SavedAddressRepository

class GetSavedAddressesUseCaseImpl(private val repository: SavedAddressRepository) :
    GetSavedAddressesUseCase {
    override suspend fun invoke(): List<Address> {
        return repository.getSavedAddresses()
    }
}