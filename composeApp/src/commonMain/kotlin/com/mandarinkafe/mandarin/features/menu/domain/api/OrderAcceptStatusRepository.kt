package com.mandarinkafe.mandarin.features.menu.domain.api

import com.mandarinkafe.mandarin.features.menu.domain.models.OrderAcceptStatusSnapshot
import com.mandarinkafe.mandarin.util.Resource

interface OrderAcceptStatusRepository {
    suspend fun loadOrderAcceptStatus(): Resource<Unit>
    suspend fun loadOrderAcceptStatusIfStale(): Resource<Unit>
    suspend fun getOrderAcceptStatus(): Resource<OrderAcceptStatusSnapshot>
    suspend fun fetchOrderAcceptStatusFresh(): OrderAcceptStatusSnapshot
}
