package com.mandarinkafe.mandarin.core.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response

interface IikoNetworkClient {
    suspend fun getLoyaltyCustomerInfo(phone: String): Response
    suspend fun getAllCustomerCategories(): Response
    suspend fun getDiscounts(): Response
    suspend fun getTerminalGroupsIds(): Response
    suspend fun getAliveTerminalGroups(terminalGroupIds: List<String>): Response
}
