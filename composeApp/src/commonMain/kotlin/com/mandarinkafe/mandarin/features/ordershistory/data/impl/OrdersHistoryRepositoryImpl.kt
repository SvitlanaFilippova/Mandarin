package com.mandarinkafe.mandarin.features.ordershistory.data.impl

import com.mandarinkafe.mandarin.features.ordershistory.data.local.OrdersHistoryStorage
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersHistoryRepository
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder

class OrdersHistoryRepositoryImpl(private val storage: OrdersHistoryStorage) :
    OrdersHistoryRepository {
    override suspend fun getOrders() = storage.getOrders()

    override suspend fun saveOrder(order: SavedOrder) {
        storage.saveOrder(order)
    }

    override suspend fun removeOrderById(id: String) {
        storage.removeOrderById(id)
    }

    override suspend fun sync() {
        TODO("Not yet implemented")
    }
}