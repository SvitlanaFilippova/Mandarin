package com.mandarinkafe.mandarin.features.orderinfo.data.impl

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ChangeOrderRepository
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Resource

class ChangeOrderRepositoryImpl(private val networkClient: IikoNetworkClient) :
    ChangeOrderRepository {
    override suspend fun cancel(id: String): Resource<Unit> {
        val response = try {
            networkClient.cancelOrder(id)
        } catch (e: Exception) {
            return Resource.ErrorOther("Ошибка сети: ${e.message}")
        }
        return when (response.resultCode) {
            HTTP_SUCCESS -> Resource.Success(Unit)

            else -> Resource.ErrorOther("Что-то пошло не так")
        }
    }
}