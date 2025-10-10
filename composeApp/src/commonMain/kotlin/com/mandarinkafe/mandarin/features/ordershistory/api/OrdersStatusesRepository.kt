package com.mandarinkafe.mandarin.features.ordershistory.domain.api

import com.mandarinkafe.mandarin.features.ordershistory.domain.models.OrderStatus
import com.mandarinkafe.mandarin.util.Resource

interface OrdersStatusesRepository {
    suspend fun getStatuses(ids: List<String>): Resource<List<OrderStatus>>
}