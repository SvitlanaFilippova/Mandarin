package com.mandarinkafe.mandarin.features.infrastructure.domain.impl

import com.mandarinkafe.mandarin.features.infrastructure.domain.api.GetPaymentTypesUseCase
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.PaymentTypesRepository
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.util.Resource

class GetPaymentTypesUseCaseImpl(private val repository: PaymentTypesRepository) :
    GetPaymentTypesUseCase {
    override suspend fun invoke(): Resource<List<PaymentType>> {
        return repository.getPaymentTypes()
    }
}