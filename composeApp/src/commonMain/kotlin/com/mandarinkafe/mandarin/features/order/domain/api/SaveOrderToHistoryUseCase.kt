package com.mandarinkafe.mandarin.features.order.domain.api

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder

interface SaveOrderToHistoryUseCase {
    suspend operator fun invoke(order: IncomingOrder, paymentMethodCode: String? = null)
}