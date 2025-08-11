package com.mandarinkafe.mandarin.features.discounts.domain.api

import com.mandarinkafe.mandarin.features.discounts.domain.models.CategoryDiscountMap

interface CategoryDiscountRepository {
    suspend fun getDiscountIdForCategory(categoryId: String): String?
    suspend fun getAllMappings(): List<CategoryDiscountMap>
    suspend fun refreshFromApi()
 }