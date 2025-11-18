package com.mandarinkafe.mandarin.features.payment.domain.impl

import com.mandarinkafe.mandarin.features.payment.domain.api.GetPaymentStatusUseCase
import com.mandarinkafe.mandarin.features.payment.domain.api.PaymentRepository
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentInfo
import com.mandarinkafe.mandarin.util.Resource

class GetPaymentStatusUseCaseImpl(
    private val repository: PaymentRepository,
) : GetPaymentStatusUseCase {

    override suspend fun invoke(orderId: String): Resource<PaymentInfo> {
        return repository.getPaymentStatus(orderId)
    }
}

