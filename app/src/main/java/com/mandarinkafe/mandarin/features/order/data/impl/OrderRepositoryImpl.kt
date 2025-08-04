package com.mandarinkafe.mandarin.features.order.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.order.data.mapper.toOrderDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.CreateDeliveryResponse
import com.mandarinkafe.mandarin.features.order.data.network.dto.paymenttype.PaymentTypesResponse
import com.mandarinkafe.mandarin.features.order.data.network.dto.paymenttype.toDomain
import com.mandarinkafe.mandarin.features.order.domain.api.OrderRepository
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrder
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.orderconfirmation.data.toDomain
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource

class OrderRepositoryImpl(private val networkClient: IikoNetworkClient) : OrderRepository {

    override suspend fun getPaymentTypes(): List<PaymentType> {
        val response = networkClient.getPaymentTypes()
        if (response.resultCode == HTTP_SUCCESS) {
            val iikoTypes = (response as PaymentTypesResponse).paymentTypes
            val domainTypes = iikoTypes.filterNot { it.isDeleted }.map { it.toDomain() }
            return domainTypes
        } else {
            return emptyList()
        }
    }

    private val logTag = "DEBUG ORDER API OrderRepository"

    override suspend fun createOrder(outgoingOrder: OutgoingOrder): Resource<IncomingOrder> {
        return try {
            val orderDto = outgoingOrder.toOrderDto()
            val response = networkClient.createDelivery(orderDto)
            Log.d(
                logTag,
                "response code: ${response.resultCode}, full response: $response"
            )

            when (response.resultCode) {
                NO_CONNECTION -> {
                    Log.d(logTag, "No connection error")
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    Log.d(logTag, "Success response, converting to domain")
                    val orderInfo = (response as CreateDeliveryResponse).orderInfo.toDomain()
                    if (orderInfo.errorInfo == null) {
                        Resource.Success(data = orderInfo)
                    } else {
                        Resource.ErrorOther(orderInfo.errorInfo.message ?: "Неизвестная ошибка")
                    }
                }

                else -> {
                    Log.e(logTag, "Server error or empty response. Code: ${response.resultCode}")
                    Resource.ErrorOther("Ошибка сервера или пустой ответ")
                }
            }
        } catch (e: Exception) {
            Log.e(logTag, "Exception in createOrder: ${e.message}", e)
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }
}
