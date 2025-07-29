package com.mandarinkafe.mandarin.features.order.domain.api

import com.mandarinkafe.mandarin.util.Resource

interface CheckDiscountByPhoneUseCase {
    suspend operator fun invoke(phone: String): Resource<Int?>
}