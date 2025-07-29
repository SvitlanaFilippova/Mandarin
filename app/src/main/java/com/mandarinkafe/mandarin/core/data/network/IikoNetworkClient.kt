package com.mandarinkafe.mandarin.core.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response

interface IikoNetworkClient {
    suspend fun getMenu(): Response
    suspend fun getLoyaltyCustomerInfo(phone: String): Response

}
