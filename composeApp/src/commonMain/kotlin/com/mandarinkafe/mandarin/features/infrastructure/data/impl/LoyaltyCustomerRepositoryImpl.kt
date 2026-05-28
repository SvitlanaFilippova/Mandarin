package com.mandarinkafe.mandarin.features.infrastructure.data.impl

import com.mandarinkafe.mandarin.core.data.network.ServerNetworkClient
import com.mandarinkafe.mandarin.features.infrastructure.data.network.PhoneDiscountResponse
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.LoyaltyCustomerRepository
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.Resource

class LoyaltyCustomerRepositoryImpl(private val networkClient: ServerNetworkClient) :
    LoyaltyCustomerRepository {
    override suspend fun getMaxDiscountPercent(phone: String): Resource<Int?> {
        val response = networkClient.getPhoneDiscount(CODE_FOR_PHONE + phone)
        return when (response.resultCode) {
            Constants.NO_CONNECTION -> Resource.ErrorNoInternet()
            Constants.HTTP_SUCCESS -> {
                Resource.Success((response as PhoneDiscountResponse).discountPercent)
            }

            else -> Resource.ErrorOther("Ошибка сервера или пустой ответ")
        }
    }

    private companion object {
        const val CODE_FOR_PHONE = "+7"
    }
}
