package com.mandarinkafe.mandarin.features.orderinfo.data.impl

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ChangeOrderRepository

class ChangeOrderRepositoryImpl(private val networkClient: IikoNetworkClient) :
    ChangeOrderRepository {
    override suspend fun cancel(id: String) {
        networkClient.cancelOrder(id)
    }
}