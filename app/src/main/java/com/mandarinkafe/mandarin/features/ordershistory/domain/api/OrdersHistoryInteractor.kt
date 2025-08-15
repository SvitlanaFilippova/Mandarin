package com.mandarinkafe.mandarin.features.ordershistory.domain.api

import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder

interface OrdersHistoryInteractor {
    suspend fun getHistory(): List<SavedOrder>
    suspend fun removeOrderById(id: String)
}