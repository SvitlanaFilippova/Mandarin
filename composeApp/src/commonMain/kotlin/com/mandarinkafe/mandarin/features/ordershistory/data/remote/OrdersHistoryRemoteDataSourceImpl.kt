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
            Napier.e("PaymentFlow: [OrdersHistoryRemoteDataSource] saveOrder - ERROR: No access token, orderId=${order.id}")
            return
        }
        try {
            val orderDto = order.toDto()
            Napier.d("PaymentFlow: [OrdersHistoryRemoteDataSource] saveOrder - orderId=${order.id}")
            Napier.d("PaymentFlow: [OrdersHistoryRemoteDataSource] saveOrder - orderDto: id=${orderDto.id}, number=${orderDto.number}, timestamp=${orderDto.timestamp}")
            Napier.d("PaymentFlow: [OrdersHistoryRemoteDataSource] saveOrder - orderDto: paymentMethodCode=${orderDto.paymentMethodCode}, orderType=${orderDto.orderType}")
            Napier.d("PaymentFlow: [OrdersHistoryRemoteDataSource] saveOrder - orderDto: addressLine1=${orderDto.addressLine1}, addressDetails=${orderDto.addressDetails}")
            Napier.d("PaymentFlow: [OrdersHistoryRemoteDataSource] saveOrder - orderDto: mealNames=${orderDto.mealNames.take(100)}...")
            
            val request = OrdersHistoryUpdateRequest(data = orderDto)
            val response = api.createOrUpdateOrder(buildAuthToken(token), request)
            
            if (response.resultCode != HTTP_SUCCESS) {
                Napier.e("PaymentFlow: [OrdersHistoryRemoteDataSource] saveOrder - ERROR: Server error, resultCode: ${response.resultCode}, orderId=${order.id}")
            } else {
                Napier.d("PaymentFlow: [OrdersHistoryRemoteDataSource] saveOrder - SUCCESS: orderId=${order.id}")
            }
        } catch (e: Exception) {
            Napier.e("PaymentFlow: [OrdersHistoryRemoteDataSource] saveOrder - EXCEPTION: orderId=${order.id}, error=${e.message}", e)
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

