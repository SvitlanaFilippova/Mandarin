package com.mandarinkafe.mandarin.features.infrastructure.domain.api

import com.mandarinkafe.mandarin.util.Resource

interface LoyaltyCustomerRepository {
    suspend fun getMaxDiscountPercent(phone: String): Resource<Int?>
}
