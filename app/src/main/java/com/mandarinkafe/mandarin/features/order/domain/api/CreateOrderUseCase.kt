package com.mandarinkafe.mandarin.features.order.domain.api

import com.mandarinkafe.mandarin.features.order.domain.models.Order

interface CreateOrderUseCase {
    suspend operator fun invoke(order: Order)
}