package com.mandarinkafe.mandarin.features.orderinfo.domain.api

interface ChangeOrderRepository {
    suspend fun cancel(id: String)
}