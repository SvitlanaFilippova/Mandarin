package com.mandarinkafe.mandarin.features.orderinfo.domain.api

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.util.Resource

interface OrderInfoRepository {
    suspend fun getOrderFromApi(id: String): Resource<IncomingOrder>
    suspend fun getOrderFromIiko(id: String): Resource<IncomingOrder>
}