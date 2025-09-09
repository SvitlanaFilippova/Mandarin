package com.mandarinkafe.mandarin.features.savedadresses.domain.impl

import com.mandarinkafe.mandarin.features.savedadresses.domain.api.RemoveAddressUseCase
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.SavedAddressRepository

class RemoveAddressUseCaseImpl(
    private val repository: SavedAddressRepository
) : RemoveAddressUseCase {
    override suspend operator fun invoke(id: String) {
        repository.removeAddress(id)
    }
}