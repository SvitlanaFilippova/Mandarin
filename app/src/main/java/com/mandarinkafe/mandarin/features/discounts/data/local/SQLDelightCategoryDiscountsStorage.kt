package com.mandarinkafe.mandarin.features.discounts.data.local

import com.mandarinkafe.mandarin.db.CategoryDiscountQueries
import com.mandarinkafe.mandarin.db.Category_discount
import com.mandarinkafe.mandarin.features.discounts.domain.models.CategoryDiscountMap

class SQLDelightCategoryDiscountsStorage(private val queries: CategoryDiscountQueries) :
    CategoryDiscountsStorage {
    override suspend fun getAll(): List<Category_discount> {
        return queries.selectAll()
            .executeAsList()
    }

    override suspend fun deleteAll() {
        queries.deleteAll()
    }

    override suspend fun selectDiscountByCategory(categoryId: String): String? {
        return queries.selectDiscountByCategory(categoryId).executeAsOneOrNull()
    }

    override suspend fun insert(map: CategoryDiscountMap) {
        queries.insertOrReplace(map.categoryId, map.discountId)
    }
}