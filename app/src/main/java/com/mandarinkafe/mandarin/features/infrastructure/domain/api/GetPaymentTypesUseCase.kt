package com.mandarinkafe.mandarin.features.infrastructure.domain.api

import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.util.Resource

interface GetPaymentTypesUseCase {
    suspend operator fun invoke(): Resource<List<PaymentType>>
}