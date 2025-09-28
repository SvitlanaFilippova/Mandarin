package com.mandarinkafe.mandarin.features.orderinfo.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.GetOrderStatusUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.OrderInfoRepository
import com.mandarinkafe.mandarin.util.Resource

class GetOrderStatusUseCaseImpl(private val repository: OrderInfoRepository) :
    GetOrderStatusUseCase {
    override suspend fun invoke(id: String): Resource<IncomingOrder> {
        return repository.getOrderFromApi(id)
    }
}