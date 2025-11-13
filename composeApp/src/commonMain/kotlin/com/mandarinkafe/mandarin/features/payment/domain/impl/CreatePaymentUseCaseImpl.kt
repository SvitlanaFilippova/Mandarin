package com.mandarinkafe.mandarin.features.payment.domain.impl

import com.mandarinkafe.mandarin.features.payment.domain.api.CreatePaymentUseCase
import com.mandarinkafe.mandarin.features.payment.domain.api.PaymentRepository
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentInfo
import com.mandarinkafe.mandarin.util.Resource

class CreatePaymentUseCaseImpl(
    private val repository: PaymentRepository,
) : CreatePaymentUseCase {

    override suspend fun invoke(
        paymentToken: String,
        orderId: String,
        amount: Double,
        currency: String,
        description: String,
    ): Resource<PaymentInfo> {
        return repository.createPayment(
            paymentToken = paymentToken,
            orderId = orderId,
            amount = amount,
            currency = currency,
            description = description
        )
    }
}

