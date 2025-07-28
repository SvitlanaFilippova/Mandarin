package com.mandarinkafe.mandarin.features.address.savedadresses.domain.api

interface RemoveAddressUseCase {
    suspend operator fun invoke(id: String)
}