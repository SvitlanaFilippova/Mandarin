package com.mandarinkafe.mandarin.features.payment.domain.api

import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentInfo
import com.mandarinkafe.mandarin.util.Resource

interface CreatePaymentUseCase {
    suspend operator fun invoke(
        paymentToken: String,
        orderId: String,
        amount: Double,
        currency: String = "RUB",
        description: String,
        returnUrl: String? = null, // URL для возврата после оплаты (для iOS "умного платежа")
    ): Resource<PaymentInfo>
}

