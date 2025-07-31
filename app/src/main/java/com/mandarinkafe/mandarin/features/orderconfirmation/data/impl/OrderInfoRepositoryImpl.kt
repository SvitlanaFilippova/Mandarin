package com.mandarinkafe.mandarin.features.orderconfirmation.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.dto.order.toDomain
import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.features.order.domain.models.OrderInfo
import com.mandarinkafe.mandarin.features.orderconfirmation.data.network.dto.OrderInfoResponse
import com.mandarinkafe.mandarin.features.orderconfirmation.domain.api.OrderInfoRepository
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class OrderInfoRepositoryImpl(private val networkClient: IikoNetworkClient) : OrderInfoRepository {
    override fun observeOrderInfo(id: String): Flow<Resource<OrderInfo>> = flow {
        while (true) {
            val response = networkClient.getOrderStatusById(id)
            val result = when (response.resultCode) {
                NO_CONNECTION -> Resource.ErrorNoInternet<OrderInfo>()
                HTTP_SUCCESS -> {
                    Log.d("DEBUG OBSERVE STATUS RepositoryImpl", "HTTP_SUCCESS")
                    val orderInfo = (response as OrderInfoResponse)
                        .orders
                        .firstOrNull { it.id == id }
                        ?.toDomain()

                    if (orderInfo != null) {
                        Resource.Success(data = orderInfo)
                    } else {
                        Resource.ErrorOther(
                            "Ошибка сервера или пустой ответ"
                        )
                    }
                }

                else -> Resource.ErrorOther("Ошибка сервера или пустой ответ")
            }

            emit(result)
            delay(ORDER_STATUS_UPD_DELAY)
        }
    }.flowOn(Dispatchers.IO)

    private companion object {
        const val ORDER_STATUS_UPD_DELAY = 10000L
    }
}