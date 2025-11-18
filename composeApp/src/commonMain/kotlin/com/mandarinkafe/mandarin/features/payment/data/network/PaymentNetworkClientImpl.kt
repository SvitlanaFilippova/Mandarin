package com.mandarinkafe.mandarin.features.payment.data.network

import com.mandarinkafe.mandarin.features.payment.data.dto.CancelPaymentRequest
import com.mandarinkafe.mandarin.features.payment.data.dto.CancelPaymentResponse
import com.mandarinkafe.mandarin.features.payment.data.dto.CreatePaymentRequest
import com.mandarinkafe.mandarin.features.payment.data.dto.CreatePaymentResponse
import com.mandarinkafe.mandarin.features.payment.data.dto.PaymentStatusResponse

class PaymentNetworkClientImpl(
    private val paymentApi: PaymentServerApi,
) : PaymentNetworkClient {

    override suspend fun createPayment(request: CreatePaymentRequest): CreatePaymentResponse {
        return paymentApi.createPayment(request)
    }

    override suspend fun getPaymentStatus(orderId: String): PaymentStatusResponse {
        return paymentApi.getPaymentStatus(orderId)
    }

    override suspend fun cancelPayment(request: CancelPaymentRequest): CancelPaymentResponse {
        return paymentApi.cancelPayment(request)
    }
}

