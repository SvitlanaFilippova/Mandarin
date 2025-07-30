package com.mandarinkafe.mandarin.features.order.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.features.order.data.mapper.toDomain
import com.mandarinkafe.mandarin.features.order.data.mapper.toOrderDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.CreateDeliveryResponse
import com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.paymenttype.PaymentTypesResponse
import com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.paymenttype.toDomain
import com.mandarinkafe.mandarin.features.order.domain.api.OrderRepository
import com.mandarinkafe.mandarin.features.order.domain.models.Order
import com.mandarinkafe.mandarin.features.order.domain.models.OrderInfo
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
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

    override suspend fun createOrder(order: Order): Resource<OrderInfo> {
        return try {
            Log.d(logTag, "createOrder called with order: $order")
            val orderDto = order.toOrderDto()
            Log.d(logTag, "Converted to DTO: $orderDto")

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
                    Resource.Success(data = (response as CreateDeliveryResponse).orderInfo.toDomain())
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


