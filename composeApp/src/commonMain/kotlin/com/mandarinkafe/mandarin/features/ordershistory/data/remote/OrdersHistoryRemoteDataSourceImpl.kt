package com.mandarinkafe.mandarin.features.ordershistory.data.remote

import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.ordershistory.data.mapper.OrdersHistoryMapper.toDomain
import com.mandarinkafe.mandarin.features.ordershistory.data.mapper.OrdersHistoryMapper.toDto
import com.mandarinkafe.mandarin.features.ordershistory.data.network.OrdersHistoryServerApi
import com.mandarinkafe.mandarin.features.ordershistory.data.network.OrdersHistoryUpdateRequest
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import com.mandarinkafe.mandarin.util.Constants.BEARER_TOKEN_TYPE
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource
import io.github.aakira.napier.Napier
import io.ktor.http.HttpStatusCode

class OrdersHistoryRemoteDataSourceImpl(
    private val api: OrdersHistoryServerApi,
    private val authRepository: AuthRepository,
) : OrdersHistoryRemoteDataSource {

    private companion object {
        fun buildAuthToken(token: String) = "$BEARER_TOKEN_TYPE $token"
    }

    override suspend fun getOrders(): Resource<List<SavedOrder>> {
        val token = authRepository.getAccessToken()
        if (token == null) {
            Napier.e("OrdersHistoryRemoteDataSource, getOrders - No access token")
            return Resource.ErrorOther("Токен авторизации не найден")
        }

        return try {
            val response = api.getOrdersHistory(buildAuthToken(token))

            when (response.resultCode) {
                NO_CONNECTION -> {
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    val orders = response.data?.map { orderDto ->
                        orderDto.toDomain()
                    } ?: emptyList()
                    Resource.Success(orders)
                }

                else -> {
                    Napier.e("OrdersHistoryRemoteDataSource, getOrders error: resultCode=${response.resultCode}")
                    Resource.ErrorOther("Ошибка сервера или пустой ответ")
                }
            }
        } catch (e: Exception) {
            Napier.e("OrdersHistoryRemoteDataSource, getOrders error: $e", e)
            Resource.ErrorOther("Ошибка при получении истории заказов: ${e.message}")
        }
    }

    override suspend fun getOrderById(id: String): SavedOrder? {
        val token = authRepository.getAccessToken() ?: return null
        return try {
            val response = api.getOrderById(buildAuthToken(token), id)
            if (response.resultCode == HTTP_SUCCESS) {
                response.data?.toDomain()
            } else {
                Napier.e(
                    "OrdersHistoryRemoteDataSource, getOrderById error: resultCode=${response.resultCode}, orderId=$id"
                )
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
            Napier.e("OrdersHistoryRemoteDataSource, saveOrder error: $e", e)
        }
    }

    override suspend fun removeOrderById(id: String) {
        val token = authRepository.getAccessToken()
        if (token == null) {
            Napier.e("OrdersHistoryRemoteDataSource, removeOrderById - No access token, orderId=$id")
            error("No access token")
        }
        try {
            val response = api.deleteOrder(buildAuthToken(token), id)
            if (response.resultCode != HTTP_SUCCESS) {
                Napier.e(
                    "OrdersHistoryRemoteDataSource, removeOrderById - Server error, resultCode=${response.resultCode}, orderId=$id"
                )
                error("Failed to delete order: ${response.resultCode}")
            }
        } catch (e: Exception) {
            Napier.e("OrdersHistoryRemoteDataSource, removeOrderById error: $e", e)
            throw e
        }
    }

    override suspend fun changePaymentMethod(
        orderId: String,
        paymentMethodCode: String,
    ): Resource<Unit> {
        val token = authRepository.getAccessToken()
        if (token == null) {
            Napier.e("OrdersHistoryRemoteDataSource, changePaymentMethod - No access token, orderId=$orderId")
            return Resource.ErrorOther("Токен авторизации не найден")
        }

        return try {
            val response =
                api.changePaymentMethod(buildAuthToken(token), orderId, paymentMethodCode)

            when (response.resultCode) {
                NO_CONNECTION -> {
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    Resource.Success(Unit)
                }

                HttpStatusCode.Unauthorized.value -> {
                    Resource.ErrorOther("Ошибка авторизации")
                }

                HttpStatusCode.NotFound.value -> {
                    Resource.ErrorOther("Заказ не найден")
                }

                else -> {
                    Napier.e(
                        "OrdersHistoryRemoteDataSource, changePaymentMethod error: " +
                                "resultCode=${response.resultCode}, orderId=$orderId"
                    )
                    Resource.ErrorOther("Ошибка сервера")
                }
            }
        } catch (e: Exception) {
            Napier.e("OrdersHistoryRemoteDataSource, changePaymentMethod error: $e", e)
            Resource.ErrorOther("Ошибка при изменении способа оплаты: ${e.message}")
        }
    }
}

