package com.mandarinkafe.mandarin.features.infrastructure.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.discounts.CustomerCategoryDto
import kotlinx.serialization.Serializable

@Serializable
data class LoyaltyCustomerResponse(
    val id: String,
    val isDeleted: Boolean? = null,
    val categories: List<CustomerCategoryDto>
) : Response()
