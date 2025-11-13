package com.mandarinkafe.mandarin.features.payment.domain.impl

import com.mandarinkafe.mandarin.features.payment.domain.api.CancelPaymentUseCase
import com.mandarinkafe.mandarin.features.payment.domain.api.PaymentRepository
import com.mandarinkafe.mandarin.util.Resource

class CancelPaymentUseCaseImpl(
    private val repository: PaymentRepository,
) : CancelPaymentUseCase {

    override suspend fun invoke(orderId: String): Resource<Boolean> {
        return repository.cancelPayment(orderId)
    }
}

