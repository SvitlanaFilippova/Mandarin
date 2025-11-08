package com.mandarinkafe.mandarin.features.ordershistory.data.remote

import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.ordershistory.data.mapper.OrdersHistoryMapper.toDomain
import com.mandarinkafe.mandarin.features.ordershistory.data.mapper.OrdersHistoryMapper.toDto
import com.mandarinkafe.mandarin.features.ordershistory.data.network.OrdersHistoryServerApi
import com.mandarinkafe.mandarin.features.ordershistory.data.network.OrdersHistoryUpdateRequest
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import io.github.aakira.napier.Napier

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
        Napier.d("SAVE_ORDER DEBUG: Starting saveOrder, orderId=${order.id}")
        val token = authRepository.getAccessToken()
        if (token == null) {
            Napier.e("SAVE_ORDER ERROR: No access token")
            return
        }
        Napier.d("SAVE_ORDER DEBUG: Token obtained, length=${token.length}, prefix=${token.take(20)}...")
        try {
            val orderDto = order.toDto()
            Napier.d("SAVE_ORDER DEBUG: Order converted to DTO, sending to server")
            val request = OrdersHistoryUpdateRequest(data = orderDto)
            val response = api.createOrUpdateOrder("Bearer $token", request)
            if (response.resultCode == com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS) {
                Napier.d("SAVE_ORDER SUCCESS: Order saved successfully, orderId=${order.id}")
            } else {
                Napier.e("SAVE_ORDER ERROR: Server error, resultCode: ${response.resultCode}, orderId=${order.id}")
            }
        } catch (e: Exception) {
            Napier.e("SAVE_ORDER ERROR: Exception while saving order, orderId=${order.id}", e)
        }
    }

    override suspend fun removeOrderById(id: String) {
        val token = authRepository.getAccessToken() ?: return
        try {
            api.deleteOrder("Bearer $token", id)
        } catch (_: Exception) {}
    }
}

