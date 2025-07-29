package com.mandarinkafe.mandarin.features.order.domain.api

import com.mandarinkafe.mandarin.features.order.domain.models.Order
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType

interface OrderRepository {
    suspend fun getPaymentTypes(): List<PaymentType>
    suspend fun createOrder(order: Order)
}