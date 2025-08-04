package com.mandarinkafe.mandarin.core.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.core.data.dto.order.OrderDto

interface IikoNetworkClient {
    suspend fun getMenu(): Response
    suspend fun getLoyaltyCustomerInfo(phone: String): Response
    suspend fun getPaymentTypes(): Response
    suspend fun createDelivery(order: OrderDto): Response
    suspend fun getOrderStatusById(id: String): Response
}
