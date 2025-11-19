package com.mandarinkafe.mandarin.features.ordershistory.domain.api

import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import com.mandarinkafe.mandarin.util.Resource

interface OrdersHistoryRepository {
    suspend fun getOrders(): Resource<List<SavedOrder>>
    suspend fun getOrderById(id: String): SavedOrder?
    suspend fun saveOrder(order: SavedOrder)
    suspend fun removeOrderById(id: String)
}