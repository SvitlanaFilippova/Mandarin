package com.mandarinkafe.mandarin.features.order.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.features.order.data.mapper.toOrderDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.paymenttype.PaymentTypesResponse
import com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.paymenttype.toDomain
import com.mandarinkafe.mandarin.features.order.domain.api.OrderRepository
import com.mandarinkafe.mandarin.features.order.domain.models.Order
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

    private val logTag = "DEBUG IIKO OrderRepositoryImpl"

    override suspend fun createOrder(order: Order): Resource<Unit> {
        return try {
            Log.d(logTag, "createOrder called")
            val response = networkClient.createDelivery(order.toOrderDto())
            Log.d(logTag, "response code: ${response.resultCode} , response $response")
            when (response.resultCode) {
                NO_CONNECTION -> Resource.ErrorNoInternet<Unit>()
                HTTP_SUCCESS -> Resource.Success<Unit>(data = Unit)
                else -> Resource.ErrorOther<Unit>("Ошибка сервера или пустой ответ")
            }
        } catch (e: Exception) {
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }
}

