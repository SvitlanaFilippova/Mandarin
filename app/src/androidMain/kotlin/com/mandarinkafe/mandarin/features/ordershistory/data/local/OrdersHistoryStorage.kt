package com.mandarinkafe.mandarin.features.ordershistory.data.local

import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder

interface OrdersHistoryStorage {
    suspend fun getOrders(): List<SavedOrder>
    suspend fun saveOrder(order: SavedOrder)
    suspend fun removeOrderById(id: String)
}