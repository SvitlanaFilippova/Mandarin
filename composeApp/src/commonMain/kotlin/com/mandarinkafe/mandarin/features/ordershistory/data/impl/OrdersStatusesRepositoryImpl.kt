package com.mandarinkafe.mandarin.features.ordershistory.data.impl

import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.ordershistory.data.mapper.OrdersHistoryMapper.toDomain
import com.mandarinkafe.mandarin.features.ordershistory.data.network.OrdersHistoryServerApi
import com.mandarinkafe.mandarin.features.ordershistory.data.network.OrdersStatusesRequest
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersStatusesRepository
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.OrderStatus
import com.mandarinkafe.mandarin.util.Constants.BEARER_TOKEN_TYPE
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Resource
import io.github.aakira.napier.Napier

class OrdersStatusesRepositoryImpl(
    private val api: OrdersHistoryServerApi,
    private val authRepository: AuthRepository,
) : OrdersStatusesRepository {

    private companion object {
        fun buildAuthToken(token: String) = "$BEARER_TOKEN_TYPE $token"
    }

    override suspend fun getStatuses(ids: List<String>): Resource<List<OrderStatus>> {
        val token = authRepository.getAccessToken()
        if (token == null) {
            return Resource.ErrorOther("Токен авторизации не найден")
        }

        return try {
            val request = OrdersStatusesRequest(orderIds = ids)
            val response = api.getOrdersStatuses(buildAuthToken(token), request)

            when (response.resultCode) {
                HTTP_SUCCESS -> {
                    val orders = response.orders?.map { it.toDomain() } ?: emptyList()
                    Resource.Success(data = orders)
                }

                else -> {
                    Napier.e("OrdersStatusesRepositoryImpl, getStatuses error: resultCode=${response.resultCode}")
                    Resource.ErrorOther("Ошибка сервера или пустой ответ")
                }
            }
        } catch (e: Exception) {
            Napier.e("OrdersStatusesRepositoryImpl, getStatuses error: $e", e)
            Resource.ErrorOther("Ошибка при получении статусов: ${e.message}")
        }
    }
}