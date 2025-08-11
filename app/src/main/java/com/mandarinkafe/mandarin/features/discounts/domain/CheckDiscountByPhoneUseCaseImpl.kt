package com.mandarinkafe.mandarin.features.discounts.domain

import com.mandarinkafe.mandarin.features.discounts.domain.api.CategoryDiscountRepository
import com.mandarinkafe.mandarin.features.discounts.domain.api.CheckDiscountByPhoneUseCase
import com.mandarinkafe.mandarin.features.discounts.domain.models.CustomerCategory
import com.mandarinkafe.mandarin.features.discounts.domain.models.CustomersMaxDiscount
import com.mandarinkafe.mandarin.features.order.domain.api.LoyaltyCustomerRepository
import com.mandarinkafe.mandarin.util.Resource

class CheckDiscountByPhoneUseCaseImpl(
    private val repository: LoyaltyCustomerRepository,
    private val categoryDiscountRepository: CategoryDiscountRepository
) : CheckDiscountByPhoneUseCase {

        override suspend fun invoke(phone: String): Resource<CustomersMaxDiscount?> {
            val resource = repository.getLoyaltyCustomerInfo(phone)
            return when (resource) {
                is Resource.Success -> {
                    val customerInfo = resource.data

                    if (customerInfo == null || customerInfo.isDeleted) {
                        Resource.Success(null)
                    } else {
                        val categoryDiscountList = categoryDiscountRepository.getAllMappings()

                        val activeCategories = customerInfo.categories.filter { it.isActive }
                        val maxCategory = activeCategories.maxByOrNull { it.name.toIntOrNull() ?: 0 }

                        if (maxCategory != null) {
                            val discountId = categoryDiscountList
                                .firstOrNull { it.categoryId == maxCategory.id }
                                ?.discountId ?: ""

                            val discountPercent = maxCategory.name.toIntOrNull() ?: 0

                            Resource.Success(
                                CustomersMaxDiscount(
                                    discountId = discountId,
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