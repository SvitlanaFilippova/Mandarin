package com.mandarinkafe.mandarin.features.order.domain.api

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrder
import com.mandarinkafe.mandarin.util.Resource

interface OrderRepository {
    suspend fun createOrder(outgoingOrder: OutgoingOrder): Resource<IncomingOrder>
}