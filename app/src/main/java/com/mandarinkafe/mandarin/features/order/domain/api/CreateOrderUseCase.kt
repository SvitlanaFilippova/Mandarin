package com.mandarinkafe.mandarin.features.order.domain.api

import com.mandarinkafe.mandarin.features.order.domain.models.Order
import com.mandarinkafe.mandarin.features.order.domain.models.OrderInfo
import com.mandarinkafe.mandarin.util.Resource

interface CreateOrderUseCase {
    suspend operator fun invoke(order: Order): Resource<OrderInfo>
}