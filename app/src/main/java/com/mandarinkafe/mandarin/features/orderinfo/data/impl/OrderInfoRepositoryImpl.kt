package com.mandarinkafe.mandarin.features.orderinfo.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.orderinfo.data.network.OrdersInfoResponse
import com.mandarinkafe.mandarin.features.orderinfo.data.toDomain
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.OrderInfoRepository
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class OrderInfoRepositoryImpl(
    private val networkClient: IikoNetworkClient,
    private val menuCache: MenuCache,
) : OrderInfoRepository {
    override fun observeOrderInfo(id: String, delay: Long): Flow<Resource<IncomingOrder>> = flow {
        while (true) {
            val result = getStatusFromApi(id)
            emit(result)
            delay(delay)
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getCurrentStatus(id: String): Resource<IncomingOrder> {
        return getStatusFromApi(id)
    }

    private suspend fun getStatusFromApi(id: String): Resource<IncomingOrder> {
        val response = networkClient.getSingleOrderInfoById(id)
        return when (response.resultCode) {
            NO_CONNECTION -> Resource.ErrorNoInternet<IncomingOrder>()
            HTTP_SUCCESS -> {
                Log.d("DEBUG OBSERVE STATUS RepositoryImpl", "HTTP_SUCCESS")
                val addons = menuCache.addonsCategories.value
                val orderInfo = (response as OrdersInfoResponse)
                    .orders
                    .firstOrNull { it.id == id }
                    ?.toDomain(addons)

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
    }
}