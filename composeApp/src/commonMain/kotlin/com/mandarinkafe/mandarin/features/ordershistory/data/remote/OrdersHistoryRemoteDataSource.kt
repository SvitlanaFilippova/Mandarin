package com.mandarinkafe.mandarin.features.ordershistory.data.remote

import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder

interface OrdersHistoryRemoteDataSource {
    suspend fun getOrders(): List<SavedOrder>
    suspend fun saveOrder(order: SavedOrder)
    suspend fun removeOrderById(id: String)
}

