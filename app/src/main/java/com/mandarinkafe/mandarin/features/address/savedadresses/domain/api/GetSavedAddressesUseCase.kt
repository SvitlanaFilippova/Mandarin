package com.mandarinkafe.mandarin.features.address.savedadresses.domain.api

import com.mandarinkafe.mandarin.core.domain.models.Address

interface GetSavedAddressesUseCase {
    suspend operator fun invoke(): List<Address>
}