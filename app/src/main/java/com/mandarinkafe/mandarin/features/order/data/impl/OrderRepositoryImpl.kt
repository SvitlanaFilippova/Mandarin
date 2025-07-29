package com.mandarinkafe.mandarin.features.order.data.impl

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.OrderDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.paymenttype.PaymentTypeIiko
import com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.paymenttype.PaymentTypesResponse
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS

class OrderRepositoryImpl(private val networkClient: IikoNetworkClient) {
    private suspend fun getPaymentTypes(): List<PaymentTypeIiko> {
        val response = networkClient.getPaymentTypes()
        return if (response.resultCode == HTTP_SUCCESS) {
            (response as PaymentTypesResponse).paymentTypes
        } else emptyList()
    }

    suspend fun createOrder() {
        OrderDto(
            phone = TODO(),
            orderServiceType = TODO(),
            deliveryPoint = TODO(),
            comment = TODO(),
            customer = TODO(),
            items = TODO(),
            payments = TODO()
        )
    }

    private companion object {
        const val CODE_FOR_PHONE = "+7"
    }
}


