package com.mandarinkafe.mandarin.features.orderinfo.domain.api

import com.mandarinkafe.mandarin.util.Resource

interface AddPaymentToOrderUseCase {
    suspend operator fun invoke(orderId: String, amount: Double): Resource<Unit>
}

