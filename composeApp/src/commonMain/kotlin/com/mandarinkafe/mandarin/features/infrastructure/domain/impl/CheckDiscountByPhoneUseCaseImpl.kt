package com.mandarinkafe.mandarin.features.infrastructure.domain.impl

import com.mandarinkafe.mandarin.features.infrastructure.domain.api.CheckDiscountByPhoneUseCase
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.LoyaltyCustomerRepository
import com.mandarinkafe.mandarin.features.infrastructure.domain.models.CustomersMaxDiscount
import com.mandarinkafe.mandarin.util.Resource

class CheckDiscountByPhoneUseCaseImpl(
    private val repository: LoyaltyCustomerRepository,
) : CheckDiscountByPhoneUseCase {
    override suspend fun invoke(phone: String): Resource<CustomersMaxDiscount?> {
        return when (val resource = repository.getMaxDiscountPercent(phone)) {
            is Resource.Success -> {
                val discountPercent = resource.data
                if (discountPercent == null || discountPercent <= 0) {
                    Resource.Success(null)
                } else {
                    Resource.Success(
                        CustomersMaxDiscount(
                            discountPercent = discountPercent
                        )
                    )
                }
            }

            is Resource.ErrorNoInternet -> Resource.ErrorNoInternet()
            else -> Resource.ErrorOther(resource.message ?: "")
        }
    }
}
