package com.mandarinkafe.mandarin.features.order.domain.impl

import com.mandarinkafe.mandarin.features.order.domain.api.GetPaymentTypesUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.OrderRepository
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType

class GetPaymentTypesUseCaseImpl(private val repository: OrderRepository) : GetPaymentTypesUseCase {
    override suspend fun invoke(): List<PaymentType> {
        return repository.getPaymentTypes()
    }
}