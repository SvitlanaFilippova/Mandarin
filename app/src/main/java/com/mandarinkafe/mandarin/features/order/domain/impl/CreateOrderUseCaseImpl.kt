package com.mandarinkafe.mandarin.features.order.domain.impl

import com.mandarinkafe.mandarin.features.order.domain.api.CreateOrderUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.OrderRepository
import com.mandarinkafe.mandarin.features.order.domain.models.Order

class CreateOrderUseCaseImpl(private val repository: OrderRepository) : CreateOrderUseCase {
    override suspend fun invoke(order: Order) {
        repository.createOrder(order)
    }
}