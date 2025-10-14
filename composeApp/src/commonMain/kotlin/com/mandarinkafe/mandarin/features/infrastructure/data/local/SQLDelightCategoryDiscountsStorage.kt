package com.mandarinkafe.mandarin.features.infrastructure.data.local

import com.mandarinkafe.mandarin.shared.database.CategoryDiscountQueries
import com.mandarinkafe.mandarin.shared.database.Category_discount
import com.mandarinkafe.mandarin.features.infrastructure.domain.models.CategoryDiscountMap

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