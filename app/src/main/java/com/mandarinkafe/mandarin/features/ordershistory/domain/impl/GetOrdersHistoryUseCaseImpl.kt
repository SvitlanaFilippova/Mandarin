package com.mandarinkafe.mandarin.features.ordershistory.domain.impl

import com.mandarinkafe.mandarin.features.ordershistory.domain.api.GetOrdersHistoryUseCase
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersHistoryRepository
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder

class GetOrdersHistoryUseCaseImpl(private val repository: OrdersHistoryRepository) :
    GetOrdersHistoryUseCase {
    override suspend fun invoke(): List<SavedOrder> {
        return repository.getOrders()
    }
}