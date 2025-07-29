package com.mandarinkafe.mandarin.features.order.data.impl

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.features.order.data.impl.mapper.toDomain
import com.mandarinkafe.mandarin.features.order.data.network.dto.LoyaltyCustomerResponse
import com.mandarinkafe.mandarin.features.order.domain.api.LoyaltyCustomerRepository
import com.mandarinkafe.mandarin.features.order.domain.models.LoyaltyCustomer
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource

class LoyaltyCustomerRepositoryImpl(private val networkClient: IikoNetworkClient) :
    LoyaltyCustomerRepository {
    override suspend fun getLoyaltyCustomerInfo(phone: String): Resource<LoyaltyCustomer> {
        val response = networkClient.getLoyaltyCustomerInfo(CODE_FOR_PHONE + phone)
        return when (response.resultCode) {
            NO_CONNECTION -> Resource.ErrorNoInternet<LoyaltyCustomer>()
            HTTP_SUCCESS -> {
                Resource.Success<LoyaltyCustomer>(data = (response as LoyaltyCustomerResponse).toDomain())
            }

            else -> Resource.ErrorOther<LoyaltyCustomer>("Ошибка сервера или пустой ответ")
        }
    }

    private companion object {
        const val CODE_FOR_PHONE = "+7"
    }
}