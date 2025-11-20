package com.mandarinkafe.mandarin.features.orderinfo.domain.impl

import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ChangePaymentMethodUseCase
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersHistoryRepository
import com.mandarinkafe.mandarin.util.Resource

class ChangePaymentMethodUseCaseImpl(
    private val ordersHistoryRepository: OrdersHistoryRepository,
) : ChangePaymentMethodUseCase {
    override suspend fun invoke(orderId: String, paymentMethodCode: String): Resource<Unit> {
        return ordersHistoryRepository.changePaymentMethod(orderId, paymentMethodCode)
    }
}

