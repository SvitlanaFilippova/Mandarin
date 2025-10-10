package com.mandarinkafe.mandarin.core.domain.api

interface ForceRefreshMenuUseCase {
    suspend operator fun invoke()
}