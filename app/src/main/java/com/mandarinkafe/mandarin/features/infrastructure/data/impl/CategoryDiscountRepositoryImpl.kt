package com.mandarinkafe.mandarin.features.infrastructure.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.features.infrastructure.data.local.CategoryDiscountsStorage
import com.mandarinkafe.mandarin.features.infrastructure.data.network.CustomerCategoriesResponse
import com.mandarinkafe.mandarin.features.infrastructure.data.network.DiscountsResponse
import com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.discounts.CustomerCategoryDto
import com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.discounts.DiscountTypeDto
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.CategoryDiscountRepository
import com.mandarinkafe.mandarin.features.infrastructure.domain.models.CategoryDiscountMap
import com.mandarinkafe.mandarin.features.infrastructure.domain.models.toDomain
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS

class CategoryDiscountRepositoryImpl(
    private val storage: CategoryDiscountsStorage,
    private val networkClient: IikoNetworkClient
) : CategoryDiscountRepository {
    override suspend fun getDiscountIdForCategory(categoryId: String): String? {
        return storage.selectDiscountByCategory(categoryId)
    }

    override suspend fun getAllMappings(): List<CategoryDiscountMap> {
        val result = storage.getAll().map { it.toDomain() }
        return result
    }

    override suspend fun refreshFromApi() {
        val categories = getCustomerCategories()
        val discounts = getDiscountTypes()
        val mapping = buildCategoryDiscountMap(categories, discounts)
        storage.deleteAll()
        mapping.forEach {
            storage.insert(it)
        }
    }

    private fun buildCategoryDiscountMap(
        categories: List<CustomerCategoryDto>,
        discounts: List<DiscountTypeDto>
    ): List<CategoryDiscountMap> {
        return categories.mapNotNull { category ->
            val categoryPercent = category.name.toDoubleOrNull()
            if (categoryPercent == null) {
                Log.w(
                    "DEBUG DISCOUNT Repository",
                    "Category '${category.name}' is not a number, skipping"
                )
                return@mapNotNull null
            }

            val matchingDiscount = discounts.firstOrNull { discount ->
                discount.percent.toDouble() == categoryPercent
            }

            matchingDiscount?.let {
                CategoryDiscountMap(category.id, it.id)
            }
        }
    }

    private suspend fun getDiscountTypes(): List<DiscountTypeDto> {
        val response = networkClient.getDiscounts()
        if (response.resultCode == HTTP_SUCCESS) {
            val rawDiscounts = (response as DiscountsResponse).discounts
            val discounts = rawDiscounts.firstOrNull()?.items
            val filtered = discounts?.filterNot { it.isDeleted } ?: emptyList()
            return filtered
        } else {
            return emptyList()
        }
    }

    private suspend fun getCustomerCategories(): List<CustomerCategoryDto> {
        val response = networkClient.getAllCustomerCategories()
        if (response.resultCode == HTTP_SUCCESS) {
            val categories = (response as CustomerCategoriesResponse).guestCategories
            return categories?.filter { it.isActive } ?: emptyList()
        } else {
            return emptyList()
        }
    }

}
