package com.mandarinkafe.mandarin.features.orderconfirmation.domain.api

import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface ObserveOrderStatusUseCase {
    operator fun invoke(id: String): Flow<Resource<String>>
}