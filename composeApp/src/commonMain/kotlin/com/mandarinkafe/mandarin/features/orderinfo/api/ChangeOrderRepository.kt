package com.mandarinkafe.mandarin.features.orderinfo.domain.api

import com.mandarinkafe.mandarin.util.Resource

interface ChangeOrderRepository {
    suspend fun cancel(id: String): Resource<Unit>
}