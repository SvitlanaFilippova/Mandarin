package com.mandarinkafe.mandarin.features.infrastructure.domain.api

import com.mandarinkafe.mandarin.features.order.domain.models.LoyaltyCustomer
import com.mandarinkafe.mandarin.util.Resource

interface LoyaltyCustomerRepository {
    suspend fun getLoyaltyCustomerInfo(phone: String): Resource<LoyaltyCustomer>
}