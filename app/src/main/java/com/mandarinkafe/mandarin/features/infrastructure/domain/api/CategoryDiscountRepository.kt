package com.mandarinkafe.mandarin.features.infrastructure.domain.api

import com.mandarinkafe.mandarin.features.infrastructure.domain.models.CategoryDiscountMap

interface CategoryDiscountRepository {
    suspend fun getDiscountIdForCategory(categoryId: String): String?
    suspend fun getAllMappings(): List<CategoryDiscountMap>
    suspend fun refreshFromApi()
 }