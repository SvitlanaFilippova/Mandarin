package com.mandarinkafe.mandarin.features.orderinfo.domain.api

import com.mandarinkafe.mandarin.util.Resource

interface ChangeOrderRepository {
    suspend fun cancel(
        id: String,
        cancelCauseId: String? = null,
        cancelComment: String? = null,
    ): Resource<Unit>

    suspend fun addPayment(orderId: String, paymentTypeId: String, amount: Double): Resource<Unit>
}