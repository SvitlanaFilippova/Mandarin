package com.mandarinkafe.mandarin.features.order.domain.impl

import com.mandarinkafe.mandarin.features.infrastructure.domain.api.CheckDiscountByPhoneUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.ApplyPhoneDiscountUseCase
import com.mandarinkafe.mandarin.features.order.domain.models.DiscountByPhoneResult
import com.mandarinkafe.mandarin.util.Resource

class ApplyPhoneDiscountUseCaseImpl(
    private val checkDiscountByPhone: CheckDiscountByPhoneUseCase,
) : ApplyPhoneDiscountUseCase {
    override suspend fun invoke(
        rawPhone: String,
        currentDiscount: Int,
    ): DiscountByPhoneResult {
        return when {
            rawPhone.length != VALID_PHONE_LENGTH && currentDiscount > 0 -> {
                DiscountByPhoneResult(discountSize = 0, shouldUpdate = true)
            }

            rawPhone.length == VALID_PHONE_LENGTH -> {
                when (val result = checkDiscountByPhone(rawPhone)) {
                    is Resource.Success -> DiscountByPhoneResult(
                        discountSize = result.data?.discountPercent ?: 0,
                        discountId = result.data?.discountId,
                        shouldUpdate = true
                    )

                    else -> DiscountByPhoneResult(discountSize = 0, shouldUpdate = false)
                }
            }

            else -> DiscountByPhoneResult(discountSize = 0, shouldUpdate = false)
        }
    }

    companion object {
        private const val VALID_PHONE_LENGTH = 10
    }
}