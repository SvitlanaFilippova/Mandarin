package com.mandarinkafe.mandarin.features.orderconfirmation.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.orderconfirmation.domain.api.ObserveOrderStatusUseCase
import com.mandarinkafe.mandarin.features.orderconfirmation.domain.api.OrderInfoRepository
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

class ObserveOrderStatusUseCaseImpl(private val repository: OrderInfoRepository) :
    ObserveOrderStatusUseCase {
    override fun invoke(id: String, delay: Long): Flow<Resource<IncomingOrder>> {
        return repository.observeOrderInfo(id, delay)
    }
}