package com.mandarinkafe.mandarin.features.orderconfirmation.data.impl

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
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
    override fun observeOrderStatus(id: String): Flow<Resource<String>> = flow {
        while (true) {
            val response = networkClient.getOrderStatusById(id)
            val result = when (response.resultCode) {
                NO_CONNECTION -> Resource.ErrorNoInternet<String>()
                HTTP_SUCCESS -> {
                    val status = (response as OrderInfoResponse).orders.first { it.id == id }.status
                    if (status != null) {
                        Resource.Success<String>(data = status)
                    } else {
                        Resource.ErrorOther<String>(
                            "Ошибка сервера или пустой ответ"
                        )
                    }
                }

                else -> Resource.ErrorOther<String>("Ошибка сервера или пустой ответ")
            }

            emit(result)
            delay(ORDER_STATUS_UPD_DELAY)
        }
    }.flowOn(Dispatchers.IO)

    private companion object {
        const val ORDER_STATUS_UPD_DELAY = 20000L
    }
}