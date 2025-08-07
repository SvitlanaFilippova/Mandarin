package com.mandarinkafe.mandarin.features.orderinfo.domain.api

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface ObserveOrderStatusUseCase {
    operator fun invoke(id: String, delay: Long): Flow<Resource<IncomingOrder>>
}