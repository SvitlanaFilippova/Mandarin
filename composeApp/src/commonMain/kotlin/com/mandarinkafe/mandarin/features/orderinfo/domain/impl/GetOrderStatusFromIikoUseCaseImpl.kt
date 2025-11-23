package com.mandarinkafe.mandarin.features.orderinfo.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.GetOrderStatusFromIikoUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.OrderInfoRepository
import com.mandarinkafe.mandarin.util.Resource

class GetOrderStatusFromIikoUseCaseImpl(private val repository: OrderInfoRepository) :
    GetOrderStatusFromIikoUseCase {
    override suspend fun invoke(id: String): Resource<IncomingOrder> {
        return repository.getOrderFromIiko(id)
    }
}



