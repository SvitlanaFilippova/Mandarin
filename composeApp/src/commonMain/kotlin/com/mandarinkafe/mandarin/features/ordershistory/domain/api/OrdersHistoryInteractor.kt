package com.mandarinkafe.mandarin.features.ordershistory.domain.api

import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import com.mandarinkafe.mandarin.util.Resource

interface OrdersHistoryInteractor {
    suspend fun getHistory(): Resource<List<SavedOrder>>
    suspend fun getOrderById(id: String): SavedOrder?
    suspend fun removeOrderById(id: String)
}