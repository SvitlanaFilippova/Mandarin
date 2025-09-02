package com.mandarinkafe.mandarin.features.infrastructure.domain.api

import com.mandarinkafe.mandarin.util.Resource

interface CheckIfTerminalIsAliveUseCase {
    suspend operator fun invoke(): Resource<Boolean>
}