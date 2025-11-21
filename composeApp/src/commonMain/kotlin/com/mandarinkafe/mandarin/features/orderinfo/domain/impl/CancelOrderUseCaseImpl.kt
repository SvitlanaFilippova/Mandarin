package com.mandarinkafe.mandarin.features.orderinfo.domain.impl

import com.mandarinkafe.mandarin.features.orderinfo.domain.api.CancelOrderUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ChangeOrderRepository
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersHistoryInteractor
import com.mandarinkafe.mandarin.features.payment.domain.api.CancelPaymentUseCase
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_ONLINE_CODE
import com.mandarinkafe.mandarin.util.Resource

class CancelOrderUseCaseImpl(
    private val repository: ChangeOrderRepository,
    private val ordersHistoryInteractor: OrdersHistoryInteractor,
    private val cancelPaymentUseCase: CancelPaymentUseCase,
) : CancelOrderUseCase {
    override suspend fun invoke(id: String, cancelCauseId: String?, cancelComment: String?): Resource<Unit> {
        // Проверяем, был ли заказ с онлайн-оплатой
        val savedOrder = ordersHistoryInteractor.getOrderById(id)
        val isOnlinePayment = savedOrder?.paymentMethodCode?.equals(PAYMENT_ONLINE_CODE, ignoreCase = true) == true

        if (isOnlinePayment) {
            // Отменяем платеж на сервере (сервер сам разберется, нужно ли что-то делать)
            cancelPaymentUseCase(id)
        }

        // Отменяем заказ
        return repository.cancel(id, cancelCauseId, cancelComment)
    }
}