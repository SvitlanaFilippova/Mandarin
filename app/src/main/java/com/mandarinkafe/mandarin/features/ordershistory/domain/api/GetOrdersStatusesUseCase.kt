package com.mandarinkafe.mandarin.features.ordershistory.domain.api

import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import com.mandarinkafe.mandarin.util.Resource

interface GetOrdersStatusesUseCase {
    suspend operator fun invoke(orders: List<SavedOrder>): Resource<List<SavedOrder>>
}