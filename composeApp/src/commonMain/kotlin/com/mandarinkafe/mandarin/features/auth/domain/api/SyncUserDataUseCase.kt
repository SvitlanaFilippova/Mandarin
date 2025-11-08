package com.mandarinkafe.mandarin.features.auth.domain.api

interface SyncUserDataUseCase {
    suspend operator fun invoke()
}