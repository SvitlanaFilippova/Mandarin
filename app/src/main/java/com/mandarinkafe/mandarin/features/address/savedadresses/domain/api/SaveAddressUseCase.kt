package com.mandarinkafe.mandarin.features.address.savedadresses.domain.api

import com.mandarinkafe.mandarin.core.domain.models.Address

interface SaveAddressUseCase {
    suspend operator fun invoke(address: Address)
}