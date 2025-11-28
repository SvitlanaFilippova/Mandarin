package com.mandarinkafe.mandarin.features.ordershistory.domain.impl

import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersHistoryInteractor
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersHistoryRepository
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import com.mandarinkafe.mandarin.util.Resource

class OrdersHistoryInteractorImpl(private val repository: OrdersHistoryRepository) :
    OrdersHistoryInteractor {
    override suspend fun getHistory(): Resource<List<SavedOrder>> {
        return repository.getOrders()
    }

    override suspend fun getOrderById(id: String): SavedOrder? {
        return repository.getOrderById(id)
    }

    override suspend fun removeOrderById(id: String) {
        repository.removeOrderById(id)
    }
}
