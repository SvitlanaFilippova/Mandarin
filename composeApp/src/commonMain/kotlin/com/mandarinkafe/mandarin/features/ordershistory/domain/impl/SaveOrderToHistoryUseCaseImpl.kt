package com.mandarinkafe.mandarin.features.ordershistory.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.order.domain.api.SaveOrderToHistoryUseCase
import com.mandarinkafe.mandarin.features.ordershistory.domain.Mapper.toSavedOrder
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersHistoryRepository

import io.github.aakira.napier.Napier

class SaveOrderToHistoryUseCaseImpl(private val repository: OrdersHistoryRepository) :
    SaveOrderToHistoryUseCase {
    override suspend fun invoke(order: IncomingOrder, paymentMethodCode: String?) {
        val savedOrder = order.toSavedOrder(paymentMethodCode)
        Napier.d("PaymentFlow: [SaveOrderToHistoryUseCase] Saving order - orderId=${savedOrder.id}, paymentMethodCode=$paymentMethodCode")
        repository.saveOrder(savedOrder)
        Napier.d("PaymentFlow: [SaveOrderToHistoryUseCase] Order saved - orderId=${savedOrder.id}")
    }
}