package com.mandarinkafe.mandarin.features.order.data.impl

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.features.order.data.mapper.toOrderDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.paymenttype.PaymentTypesResponse
import com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.paymenttype.toDomain
import com.mandarinkafe.mandarin.features.order.domain.api.OrderRepository
import com.mandarinkafe.mandarin.features.order.domain.models.Order
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS

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

    override suspend fun createOrder(order: Order) {
        networkClient.createDelivery(order.toOrderDto())
    }
}


