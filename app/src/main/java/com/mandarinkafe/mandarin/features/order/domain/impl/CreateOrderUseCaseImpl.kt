package com.mandarinkafe.mandarin.features.order.domain.impl

import com.mandarinkafe.mandarin.features.order.domain.api.CreateOrderUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.OrderRepository
import com.mandarinkafe.mandarin.features.order.domain.models.Order
import com.mandarinkafe.mandarin.features.order.domain.models.OrderInfo
import com.mandarinkafe.mandarin.util.Resource

class CreateOrderUseCaseImpl(private val repository: OrderRepository) : CreateOrderUseCase {
    override suspend fun invoke(order: Order): Resource<OrderInfo> {
        return repository.createOrder(order)
    }
}