package com.mandarinkafe.mandarin.features.discounts.domain.api

import com.mandarinkafe.mandarin.features.discounts.domain.models.CustomerCategory
import com.mandarinkafe.mandarin.features.discounts.domain.models.CustomersMaxDiscount
import com.mandarinkafe.mandarin.util.Resource

interface CheckDiscountByPhoneUseCase {
    suspend operator fun invoke(phone: String): Resource<CustomersMaxDiscount?>
}