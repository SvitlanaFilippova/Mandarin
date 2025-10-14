package com.mandarinkafe.mandarin.features.infrastructure.data.mapper

import com.mandarinkafe.mandarin.features.infrastructure.data.network.LoyaltyCustomerResponse
import com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.discounts.CustomerCategoryDto
import com.mandarinkafe.mandarin.features.infrastructure.domain.models.CustomerCategory
import com.mandarinkafe.mandarin.features.order.domain.models.LoyaltyCustomer

fun LoyaltyCustomerResponse.toDomain() = LoyaltyCustomer(
    id = id ?: "",
    isDeleted = isDeleted == true,
    categories = categories?.map { it.toDomain() } ?: emptyList(),
)

fun CustomerCategoryDto.toDomain() = CustomerCategory(
    id = id ?: "",
    name = name ?: "",
    discountPercent = name?.toIntOrNull(),
    isActive = isActive == true
)

