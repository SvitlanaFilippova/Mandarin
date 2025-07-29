package com.mandarinkafe.mandarin.features.order.domain.impl

import com.mandarinkafe.mandarin.features.order.domain.api.CheckDiscountByPhoneUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.LoyaltyCustomerRepository
import com.mandarinkafe.mandarin.util.Resource

class CheckDiscountByPhoneUseCaseImpl(private val repository: LoyaltyCustomerRepository) :
    CheckDiscountByPhoneUseCase {
    override suspend fun invoke(phone: String): Resource<Int?> {
        val resource = repository.getLoyaltyCustomerInfo(phone)
        return when (resource) {
            is Resource.Success -> {
                val customerInfo = resource.data
                val discount =
                    if (customerInfo == null || customerInfo.isDeleted) 0 else customerInfo.maxDiscountPercent
                Resource.Success(data = discount)
            }

            is Resource.ErrorNoInternet -> Resource.ErrorNoInternet()
            else -> Resource.ErrorOther(resource.message ?: "")
        }
    }
}