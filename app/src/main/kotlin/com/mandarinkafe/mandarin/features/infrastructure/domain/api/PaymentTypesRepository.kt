package com.mandarinkafe.mandarin.features.infrastructure.domain.api

import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.util.Resource

interface PaymentTypesRepository {
    suspend fun getPaymentTypes(): Resource<List<PaymentType>>
}