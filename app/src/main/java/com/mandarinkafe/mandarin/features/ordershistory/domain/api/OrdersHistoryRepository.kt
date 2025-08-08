package com.mandarinkafe.mandarin.features.ordershistory.domain.api

import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder

interface OrdersHistoryRepository {
    suspend fun getOrders(): List<SavedOrder>
    suspend fun saveOrder(order: SavedOrder)
}