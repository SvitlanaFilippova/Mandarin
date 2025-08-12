package com.mandarinkafe.mandarin.features.orderinfo.domain.api

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.util.Resource

interface GetCurrentStatusUseCase {
    suspend operator fun invoke(id: String): Resource<IncomingOrder>
}