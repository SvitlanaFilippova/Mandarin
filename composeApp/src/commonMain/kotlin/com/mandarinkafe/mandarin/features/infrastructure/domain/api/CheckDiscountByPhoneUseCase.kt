package com.mandarinkafe.mandarin.features.infrastructure.domain.api

import com.mandarinkafe.mandarin.features.infrastructure.domain.models.CustomersMaxDiscount
import com.mandarinkafe.mandarin.util.Resource

interface CheckDiscountByPhoneUseCase {
    suspend operator fun invoke(phone: String): Resource<CustomersMaxDiscount?>
}