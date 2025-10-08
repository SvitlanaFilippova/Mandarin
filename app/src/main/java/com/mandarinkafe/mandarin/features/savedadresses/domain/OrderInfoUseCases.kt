package com.mandarinkafe.mandarin.features.savedadresses.domain

import com.mandarinkafe.mandarin.features.infrastructure.domain.api.CheckIfTerminalIsAliveUseCase
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.GetPaymentTypesUseCase
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.GetSavedAddressesUseCase

data class OrderInfoUseCases(
    val getPaymentTypesUseCase: GetPaymentTypesUseCase,
    val checkIfTerminalIsAlive: CheckIfTerminalIsAliveUseCase,
    val getSavedAddressesUseCase: GetSavedAddressesUseCase,
)