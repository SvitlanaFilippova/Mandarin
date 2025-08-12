package com.mandarinkafe.mandarin.features.orderinfo.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.GetCurrentStatusUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.OrderInfoRepository
import com.mandarinkafe.mandarin.util.Resource

class GetCurrentStatusUseCaseImpl(private val repository: OrderInfoRepository) :
    GetCurrentStatusUseCase {
    override suspend fun invoke(id: String): Resource<IncomingOrder> {
        return repository.getCurrentStatus(id)
    }
}