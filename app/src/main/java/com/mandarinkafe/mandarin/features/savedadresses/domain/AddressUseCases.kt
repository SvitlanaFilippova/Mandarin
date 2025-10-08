package com.mandarinkafe.mandarin.features.savedadresses.domain

import com.mandarinkafe.mandarin.features.savedadresses.domain.api.GetSavedAddressesUseCase
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.RemoveAddressUseCase

data class AddressUseCases(
    val getSavedAddressesUseCase: GetSavedAddressesUseCase,
    val removeAddress: RemoveAddressUseCase,
)