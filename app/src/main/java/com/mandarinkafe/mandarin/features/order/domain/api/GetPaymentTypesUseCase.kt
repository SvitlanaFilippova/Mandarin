package com.mandarinkafe.mandarin.features.order.domain.api

import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType

interface GetPaymentTypesUseCase {
    suspend operator fun invoke(): List<PaymentType>
}