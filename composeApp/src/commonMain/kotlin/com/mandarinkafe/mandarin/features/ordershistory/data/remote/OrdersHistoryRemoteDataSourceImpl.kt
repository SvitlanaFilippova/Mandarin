package com.mandarinkafe.mandarin.features.ordershistory.data.remote

import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.ordershistory.data.mapper.OrdersHistoryMapper.toDomain
import com.mandarinkafe.mandarin.features.ordershistory.data.mapper.OrdersHistoryMapper.toDto
import com.mandarinkafe.mandarin.features.ordershistory.data.network.OrdersHistoryServerApi
import com.mandarinkafe.mandarin.features.ordershistory.data.network.OrdersHistoryUpdateRequest
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import com.mandarinkafe.mandarin.util.Constants.BEARER_TOKEN_TYPE
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import io.github.aakira.napier.Napier

class OrdersHistoryRemoteDataSourceImpl(
    private val api: OrdersHistoryServerApi,
    private val authRepository: AuthRepository,
) : OrdersHistoryRemoteDataSource {

    private companion object {
        fun buildAuthToken(token: String) = "$BEARER_TOKEN_TYPE $token"
    }

    override suspend fun getOrders(): List<SavedOrder> {
        val token = authRepository.getAccessToken() ?: return emptyList()
        return try {
            val response = api.getOrdersHistory(buildAuthToken(token))
            response.data?.map { orderDto ->
                orderDto.toDomain()
            } ?: emptyList()
        } catch (e: Exception) {
            Napier.e("OrdersHistoryRemoteDataSource, getOrders error: $e")
            emptyList()
        }
    }

    override suspend fun saveOrder(order: SavedOrder) {
        val token = authRepository.getAccessToken()
        if (token == null) {
            Napier.e("SAVE_ORDER ERROR: No access token")
            return
        }
        try {
            val orderDto = order.toDto()
            val request = OrdersHistoryUpdateRequest(data = orderDto)
            val response = api.createOrUpdateOrder(buildAuthToken(token), request)
            if (response.resultCode != HTTP_SUCCESS) {
                Napier.e("SAVE_ORDER ERROR: Server error, resultCode: ${response.resultCode}, orderId=${order.id}")
            }
        } catch (e: Exception) {
            Napier.e("SAVE_ORDER ERROR: Exception while saving order, orderId=${order.id}", e)
        }
    }

    override suspend fun removeOrderById(id: String) {
        val token = authRepository.getAccessToken() ?: return
        try {
            api.deleteOrder(buildAuthToken(token), id)
        } catch (e: Exception) {
            Napier.e("OrdersHistoryRemoteDataSource, removeOrderById error: $e")
        }
    }
}

