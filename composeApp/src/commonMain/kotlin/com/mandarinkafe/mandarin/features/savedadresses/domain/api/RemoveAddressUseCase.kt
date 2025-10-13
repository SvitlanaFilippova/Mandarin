package com.mandarinkafe.mandarin.features.savedadresses.domain.api

interface RemoveAddressUseCase {
    suspend operator fun invoke(id: String)
}