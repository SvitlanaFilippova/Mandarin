package com.mandarinkafe.mandarin.features.payment.data.network

import com.mandarinkafe.mandarin.features.payment.data.dto.CancelPaymentRequest
import com.mandarinkafe.mandarin.features.payment.data.dto.CancelPaymentResponse
import com.mandarinkafe.mandarin.features.payment.data.dto.CreatePaymentRequest
import com.mandarinkafe.mandarin.features.payment.data.dto.CreatePaymentResponse
import com.mandarinkafe.mandarin.features.payment.data.dto.PaymentStatusResponse

interface PaymentNetworkClient {
    suspend fun createPayment(request: CreatePaymentRequest): CreatePaymentResponse
    suspend fun getPaymentStatus(orderId: String): PaymentStatusResponse
    suspend fun cancelPayment(request: CancelPaymentRequest): CancelPaymentResponse
}

