package com.mandarinkafe.mandarin.features.order.domain.models

import com.mandarinkafe.mandarin.features.infrastructure.domain.models.CustomerCategory

data class LoyaltyCustomer(
    val id: String,
    val isDeleted: Boolean,
    val categories: List<CustomerCategory>,
)