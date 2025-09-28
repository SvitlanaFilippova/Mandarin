package com.mandarinkafe.mandarin.features.ordershistory.data.impl

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.features.orderinfo.data.network.OrdersInfoResponse
import com.mandarinkafe.mandarin.features.orderinfo.data.toOrderStatus
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersStatusesRepository
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.OrderStatus
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource

class OrdersStatusesRepositoryImpl(private val networkClient: IikoNetworkClient) :
    OrdersStatusesRepository {

    override suspend fun getStatuses(ids: List<String>): Resource<List<OrderStatus>> {
        val response = networkClient.getOrdersStatusesByIds(ids)

        return when (response.resultCode) {
            NO_CONNECTION -> {
                Resource.ErrorNoInternet()
            }
            HTTP_SUCCESS -> {
                val orders = (response as OrdersInfoResponse)
                    .orders.map { it.toOrderStatus() }

                Resource.Success(data = orders)
            }

            else -> {
                Resource.ErrorOther("Ошибка сервера или пустой ответ")
            }
        }
    }
}