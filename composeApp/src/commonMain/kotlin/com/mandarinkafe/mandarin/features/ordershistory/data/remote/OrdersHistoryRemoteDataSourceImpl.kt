package com.mandarinkafe.mandarin.features.ordershistory.data.remote

import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.ordershistory.data.mapper.OrdersHistoryMapper.toDomain
import com.mandarinkafe.mandarin.features.ordershistory.data.mapper.OrdersHistoryMapper.toDto
import com.mandarinkafe.mandarin.features.ordershistory.data.network.OrdersHistoryServerApi
import com.mandarinkafe.mandarin.features.ordershistory.data.network.OrdersHistoryUpdateRequest
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder

class OrdersHistoryRemoteDataSourceImpl(
    private val api: OrdersHistoryServerApi,
    private val authRepository: AuthRepository,
) : OrdersHistoryRemoteDataSource {

    override suspend fun getOrders(): List<SavedOrder> {
        val token = authRepository.getAccessToken() ?: return emptyList()
        return try {
            val response = api.getOrdersHistory("Bearer $token")
            response.data?.map { orderDto ->
                orderDto.toDomain()
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun saveOrder(order: SavedOrder) {
        val token = authRepository.getAccessToken() ?: return
        try {
            val orderDto = order.toDto()
            val request = OrdersHistoryUpdateRequest(data = orderDto)
            api.createOrUpdateOrder("Bearer $token", request)
        } catch (_: Exception) {}
    }

    override suspend fun removeOrderById(id: String) {
        val token = authRepository.getAccessToken() ?: return
        try {
            api.deleteOrder("Bearer $token", id)
        } catch (_: Exception) {}
    }
}

