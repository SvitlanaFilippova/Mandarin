package com.mandarinkafe.mandarin.core.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingOrderDto

interface IikoNetworkClient {
    suspend fun getMenu(): Response
    suspend fun getLoyaltyCustomerInfo(phone: String): Response
    suspend fun getPaymentTypes(): Response
    suspend fun createDelivery(order: OutgoingOrderDto): Response
    suspend fun getOrderStatusById(id: String): Response
    suspend fun getAllCustomerCategories(): Response
    suspend fun getDiscounts(): Response

}
