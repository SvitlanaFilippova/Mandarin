package com.mandarinkafe.mandarin.features.savedadresses.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.SaveAddressUseCase
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.SavedAddressRepository

class SaveAddressUseCaseImpl(private val repository: SavedAddressRepository) : SaveAddressUseCase {
    override suspend operator fun invoke(address: Address) {
        repository.saveAddress(address)
    }
}