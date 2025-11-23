package com.mandarinkafe.mandarin.features.orderinfo.domain.api

import com.mandarinkafe.mandarin.features.orderinfo.domain.models.IncomingOrderItem
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.RepeatOrderResult

interface RepeatOrderInteractor {
    suspend fun mapToCartItems(incoming: List<IncomingOrderItem>): RepeatOrderResult
    suspend fun repeatOrder(incoming: List<IncomingOrderItem>): RepeatOrderResult
}