package com.mandarinkafe.mandarin.features.ordershistory.domain.api

import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder

interface GetOrdersHistoryUseCase {
    suspend operator fun invoke(): List<SavedOrder>
}