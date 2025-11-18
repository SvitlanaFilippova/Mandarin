package com.mandarinkafe.mandarin.features.payment.domain.api

import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentInfo
import com.mandarinkafe.mandarin.util.Resource

interface PaymentRepository {
    suspend fun createPayment(
        paymentToken: String,
        orderId: String,
        amount: Double,
        currency: String,
        description: String,
        returnUrl: String? = null, // URL для возврата после оплаты (для iOS "умного платежа")
    ): Resource<PaymentInfo>

    suspend fun getPaymentStatus(orderId: String): Resource<PaymentInfo>

    suspend fun cancelPayment(orderId: String): Resource<Boolean>
}

