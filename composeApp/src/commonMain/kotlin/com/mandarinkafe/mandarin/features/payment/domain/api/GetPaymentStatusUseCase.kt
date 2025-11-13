package com.mandarinkafe.mandarin.features.payment.domain.api

import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentInfo
import com.mandarinkafe.mandarin.util.Resource

interface GetPaymentStatusUseCase {
    suspend operator fun invoke(orderId: String): Resource<PaymentInfo>
}

