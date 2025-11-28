package com.mandarinkafe.mandarin.features.infrastructure.data.local

import com.mandarinkafe.mandarin.features.infrastructure.domain.models.CategoryDiscountMap
import com.mandarinkafe.mandarin.database.Category_discount

interface CategoryDiscountsStorage {
    suspend fun getAll(): List<Category_discount>
    suspend fun selectDiscountByCategory(categoryId: String): String?
    suspend fun deleteAll()
    suspend fun insert(map: CategoryDiscountMap)
}