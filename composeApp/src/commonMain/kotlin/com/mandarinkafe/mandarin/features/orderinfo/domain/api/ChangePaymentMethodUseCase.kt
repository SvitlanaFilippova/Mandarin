package com.mandarinkafe.mandarin.features.orderinfo.domain.api

import com.mandarinkafe.mandarin.util.Resource

interface ChangePaymentMethodUseCase {
    suspend operator fun invoke(orderId: String, paymentMethodCode: String): Resource<Unit>
}

