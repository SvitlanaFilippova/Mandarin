package com.mandarinkafe.mandarin.features.discounts.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.discounts.data.network.dto.CustomerCategoryDto

data class CustomerCategoriesResponse(
    val guestCategories: List<CustomerCategoryDto>
) :Response()