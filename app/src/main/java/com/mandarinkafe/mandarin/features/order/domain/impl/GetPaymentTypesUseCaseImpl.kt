package com.mandarinkafe.mandarin.features.order.domain.impl

import com.mandarinkafe.mandarin.features.order.domain.api.GetPaymentTypesUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.OrderRepository
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.util.Resource

class GetPaymentTypesUseCaseImpl(private val repository: OrderRepository) : GetPaymentTypesUseCase {
    override suspend fun invoke(): Resource<List<PaymentType>> {
        return repository.getPaymentTypes()
    }
}