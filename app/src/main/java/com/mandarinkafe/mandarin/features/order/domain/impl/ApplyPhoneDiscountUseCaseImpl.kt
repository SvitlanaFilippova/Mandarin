package com.mandarinkafe.mandarin.features.order.domain.impl

import com.mandarinkafe.mandarin.features.order.domain.api.ApplyPhoneDiscountUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.CheckDiscountByPhoneUseCase
import com.mandarinkafe.mandarin.features.order.domain.models.PhoneDiscountResult
import com.mandarinkafe.mandarin.util.Resource

class ApplyPhoneDiscountUseCaseImpl(
    private val checkDiscountByPhone: CheckDiscountByPhoneUseCase
) : ApplyPhoneDiscountUseCase {
    override suspend fun invoke(
        rawPhone: String,
        currentDiscount: Int
    ): PhoneDiscountResult {
        val digitsOnly = rawPhone.filter { it.isDigit() }
        val normalized = when {
            digitsOnly.startsWith("7") -> digitsOnly.drop(1)
            digitsOnly.startsWith("8") -> digitsOnly.drop(1)
            else -> digitsOnly
        }
        val phone = normalized.take(VALID_PHONE_LENGTH)

        return when {
            phone.length != VALID_PHONE_LENGTH && currentDiscount > 0 -> {
                PhoneDiscountResult(phone, discountSize = 0, shouldUpdate = true)
            }

            phone.length == VALID_PHONE_LENGTH -> {
                when (val result = checkDiscountByPhone(phone)) {
                    is Resource.Success -> PhoneDiscountResult(
                        phone = phone,
                        discountSize = result.data ?: 0,
                        shouldUpdate = true
                    )

                    else -> PhoneDiscountResult(phone, discountSize = 0, shouldUpdate = false)
                }
            }

            else -> PhoneDiscountResult(phone, discountSize = 0, shouldUpdate = false)
        }
    }

    companion object {
        private const val VALID_PHONE_LENGTH = 10
    }
}