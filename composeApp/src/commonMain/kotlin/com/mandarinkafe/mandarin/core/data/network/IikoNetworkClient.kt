package com.mandarinkafe.mandarin.core.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingOrderDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingPaymentDto

interface IikoNetworkClient {
    suspend fun getLoyaltyCustomerInfo(phone: String): Response
    suspend fun createDelivery(order: OutgoingOrderDto): Response
    suspend fun getSingleOrderInfoById(id: String): Response
    suspend fun getOrdersStatusesByIds(ids: List<String>): Response
    suspend fun getAllCustomerCategories(): Response
    suspend fun getDiscounts(): Response
    suspend fun cancelOrder(id: String, cancelCauseId: String? = null, cancelComment: String? = null): Response
    suspend fun getTerminalGroupsIds(): Response
    suspend fun getAliveTerminalGroups(terminalGroupIds: List<String>): Response
    suspend fun addPayments(orderId: String, payment: OutgoingPaymentDto): Response
}
