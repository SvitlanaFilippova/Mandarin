package com.mandarinkafe.mandarin.features.infrastructure.domain.impl

import com.mandarinkafe.mandarin.features.infrastructure.domain.api.CheckDiscountByPhoneUseCase
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.LoyaltyCustomerRepository
import com.mandarinkafe.mandarin.features.infrastructure.domain.models.CustomersMaxDiscount
import com.mandarinkafe.mandarin.util.Resource

class CheckDiscountByPhoneUseCaseImpl(
    private val repository: LoyaltyCustomerRepository,
) : CheckDiscountByPhoneUseCase {
    override suspend fun invoke(phone: String): Resource<CustomersMaxDiscount?> {
        val resource = repository.getLoyaltyCustomerInfo(phone)
        return when (resource) {
            is Resource.Success -> {
                val customerInfo = resource.data

                if (customerInfo == null || customerInfo.isDeleted) {
                    Resource.Success(null)
                } else {
                    val activeCategories = customerInfo.categories.filter { it.isActive }
                    val maxCategory = activeCategories.maxByOrNull { it.name.toIntOrNull() ?: 0 }

                    if (maxCategory != null) {
                        val discountPercent = maxCategory.name.toIntOrNull() ?: 0

                        Resource.Success(
                            CustomersMaxDiscount(
                                discountPercent = discountPercent
                            )
                        )
                    } else {
                        Resource.Success(null)
                    }
                }
            }

            is Resource.ErrorNoInternet -> Resource.ErrorNoInternet()
            else -> Resource.ErrorOther(resource.message ?: "")
        }
    }
}