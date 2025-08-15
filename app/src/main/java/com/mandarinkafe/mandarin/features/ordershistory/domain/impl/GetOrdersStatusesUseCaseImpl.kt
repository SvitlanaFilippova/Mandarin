package com.mandarinkafe.mandarin.features.ordershistory.domain.impl

import com.mandarinkafe.mandarin.features.ordershistory.domain.api.GetOrdersStatusesUseCase
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersStatusesRepository
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.OrderStatus
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import com.mandarinkafe.mandarin.util.Resource

class GetOrdersStatusesUseCaseImpl(private val repository: OrdersStatusesRepository) :
    GetOrdersStatusesUseCase {
    override suspend fun invoke(orders: List<SavedOrder>): Resource<List<SavedOrder>> {
        val response = repository.getStatuses(orders.map { it.id })

        return if (response is Resource.Success) {
            if (response.data != null) {
                val ordersStatuses: List<OrderStatus> = response.data

                val statusMap = ordersStatuses.associateBy(
                    keySelector = { it.orderId },
                    valueTransform = { it.status }
                )

                val updatedOrders = orders.map { savedOrder ->
                    val deliveryStatus = statusMap[savedOrder.id]
                    savedOrder.copy(
                        status = deliveryStatus
                    )
                }

                Resource.Success(updatedOrders)
            } else {
                Resource.ErrorEmptyData()
            }
        } else {
            Resource.ErrorEmptyData()
        }
    }
}