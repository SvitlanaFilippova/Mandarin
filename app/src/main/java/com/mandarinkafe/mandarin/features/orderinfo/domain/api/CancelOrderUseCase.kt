package com.mandarinkafe.mandarin.features.orderinfo.domain.api

interface CancelOrderUseCase {
    suspend operator fun invoke(id: String)
}