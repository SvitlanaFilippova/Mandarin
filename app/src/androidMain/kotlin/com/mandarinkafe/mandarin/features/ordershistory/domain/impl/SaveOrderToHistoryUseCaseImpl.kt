package com.mandarinkafe.mandarin.features.ordershistory.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.order.domain.api.SaveOrderToHistoryUseCase
import com.mandarinkafe.mandarin.features.ordershistory.domain.Mapper.toSavedOrder
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersHistoryRepository

class SaveOrderToHistoryUseCaseImpl(private val repository: OrdersHistoryRepository) :
    SaveOrderToHistoryUseCase {
    override suspend fun invoke(order: IncomingOrder) {
        repository.saveOrder(order.toSavedOrder())
    }
}