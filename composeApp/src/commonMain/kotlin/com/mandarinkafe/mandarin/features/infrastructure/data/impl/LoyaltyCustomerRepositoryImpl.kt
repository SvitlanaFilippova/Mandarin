package com.mandarinkafe.mandarin.features.infrastructure.data.impl

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.features.infrastructure.data.network.LoyaltyCustomerResponse
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.LoyaltyCustomerRepository
import com.mandarinkafe.mandarin.features.infrastructure.data.mapper.toDomain
import com.mandarinkafe.mandarin.features.order.domain.models.LoyaltyCustomer
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.Resource

class LoyaltyCustomerRepositoryImpl(private val networkClient: IikoNetworkClient) :
    LoyaltyCustomerRepository {
    override suspend fun getLoyaltyCustomerInfo(phone: String): Resource<LoyaltyCustomer> {
        val response = networkClient.getLoyaltyCustomerInfo(CODE_FOR_PHONE + phone)
        return when (response.resultCode) {
            Constants.NO_CONNECTION -> Resource.ErrorNoInternet<LoyaltyCustomer>()
            Constants.HTTP_SUCCESS -> {
                Resource.Success<LoyaltyCustomer>(data = (response as LoyaltyCustomerResponse).toDomain())
            }

            else -> Resource.ErrorOther<LoyaltyCustomer>("Ошибка сервера или пустой ответ")
        }
    }

    private companion object {
        const val CODE_FOR_PHONE = "+7"
    }
}