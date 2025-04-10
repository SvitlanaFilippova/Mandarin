package com.mandarinkafe.mandarin.core.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response

interface NetworkClient {
    suspend fun getMenu(): Response
}
