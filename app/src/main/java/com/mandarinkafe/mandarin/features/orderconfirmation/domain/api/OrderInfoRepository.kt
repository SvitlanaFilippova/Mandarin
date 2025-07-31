package com.mandarinkafe.mandarin.features.orderconfirmation.domain.api

import com.mandarinkafe.mandarin.features.order.domain.models.OrderInfo
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface OrderInfoRepository {
    fun observeOrderInfo(id: String): Flow<Resource<OrderInfo>>
}