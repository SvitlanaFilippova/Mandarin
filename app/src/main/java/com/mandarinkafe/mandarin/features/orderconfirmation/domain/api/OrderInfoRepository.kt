package com.mandarinkafe.mandarin.features.orderconfirmation.domain.api

import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface OrderInfoRepository {
    fun observeOrderStatus(id: String): Flow<Resource<String>>
}