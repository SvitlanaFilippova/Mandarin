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

    override suspend fun getOrderById(id: String): SavedOrder? {
        val token = authRepository.getAccessToken() ?: return null
        return try {
            val response = api.getOrderById(buildAuthToken(token), id)
            if (response.resultCode == HTTP_SUCCESS) {
                response.data?.toDomain()
            } else {
                Napier.e("OrdersHistoryRemoteDataSource, getOrderById error: resultCode=${response.resultCode}, orderId=$id")
                null
            }
        } catch (e: Exception) {
            Napier.e("OrdersHistoryRemoteDataSource, getOrderById error: $e")
            null
        }
    }

    override suspend fun saveOrder(order: SavedOrder) {
        val token = authRepository.getAccessToken()
        if (token == null) {
            return
        }
        try {
            val orderDto = order.toDto()
            val request = OrdersHistoryUpdateRequest(data = orderDto)
            api.createOrUpdateOrder(buildAuthToken(token), request)
        } catch (e: Exception) {
        }
    }

    override suspend fun removeOrderById(id: String) {
        val token = authRepository.getAccessToken()
        if (token == null) {
            Napier.e("OrdersHistoryRemoteDataSource, removeOrderById - No access token, orderId=$id")
            throw IllegalStateException("No access token")
        }
        try {
            val response = api.deleteOrder(buildAuthToken(token), id)
            if (response.resultCode != HTTP_SUCCESS) {
                Napier.e("OrdersHistoryRemoteDataSource, removeOrderById - Server error, resultCode=${response.resultCode}, orderId=$id")
                throw IllegalStateException("Failed to delete order: ${response.resultCode}")
            }
        } catch (e: Exception) {
            Napier.e("OrdersHistoryRemoteDataSource, removeOrderById error: $e", e)
            throw e
        }
    }
}

