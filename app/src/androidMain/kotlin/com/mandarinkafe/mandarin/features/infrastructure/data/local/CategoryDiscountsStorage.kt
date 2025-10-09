package com.mandarinkafe.mandarin.features.infrastructure.data.local

import com.mandarinkafe.mandarin.db.Category_discount
import com.mandarinkafe.mandarin.features.infrastructure.domain.models.CategoryDiscountMap

interface CategoryDiscountsStorage {
    suspend fun getAll(): List<Category_discount>
    suspend fun selectDiscountByCategory(categoryId: String): String?
    suspend fun deleteAll()
    suspend fun insert(map: CategoryDiscountMap)
}