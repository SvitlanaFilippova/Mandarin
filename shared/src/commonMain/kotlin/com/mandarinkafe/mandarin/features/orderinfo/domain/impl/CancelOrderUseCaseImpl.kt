package com.mandarinkafe.mandarin.features.orderinfo.domain.impl

import com.mandarinkafe.mandarin.features.orderinfo.domain.api.CancelOrderUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ChangeOrderRepository
import com.mandarinkafe.mandarin.util.Resource

class CancelOrderUseCaseImpl(private val repository: ChangeOrderRepository) : CancelOrderUseCase {
    override suspend fun invoke(id: String): Resource<Unit> {
        return repository.cancel(id)
    }
}