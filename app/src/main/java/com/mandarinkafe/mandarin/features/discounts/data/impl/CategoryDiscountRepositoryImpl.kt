package com.mandarinkafe.mandarin.features.discounts.data.impl

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.features.discounts.data.local.CategoryDiscountsStorage
import com.mandarinkafe.mandarin.features.discounts.data.network.CustomerCategoriesResponse
import com.mandarinkafe.mandarin.features.discounts.data.network.DiscountsResponse
import com.mandarinkafe.mandarin.features.discounts.data.network.dto.CustomerCategoryDto
import com.mandarinkafe.mandarin.features.discounts.data.network.dto.DiscountTypeDto
import com.mandarinkafe.mandarin.features.discounts.domain.api.CategoryDiscountRepository
import com.mandarinkafe.mandarin.features.discounts.domain.models.CategoryDiscountMap
import com.mandarinkafe.mandarin.features.discounts.domain.models.toDomain
import com.mandarinkafe.mandarin.features.order.data.network.dto.paymenttype.toDomain
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS

class CategoryDiscountRepositoryImpl(
    private val storage: CategoryDiscountsStorage,
    private val networkClient: IikoNetworkClient
) : CategoryDiscountRepository {
    override suspend fun getDiscountIdForCategory(categoryId: String): String? {
        return storage.selectDiscountByCategory(categoryId)
    }

    override suspend fun getAllMappings(): List<CategoryDiscountMap> {
        return storage.getAll().map { it.toDomain() }
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
            val matchingDiscount = discounts.firstOrNull { discount ->
                discount.name == category.name
            }
            matchingDiscount?.let {
                CategoryDiscountMap(category.id, it.id)
            }
        }
    }

    private suspend fun getDiscountTypes(): List<DiscountTypeDto> {
        val response = networkClient.getDiscounts()
        if (response.resultCode == HTTP_SUCCESS) {
            val discounts = (response as DiscountsResponse).discounts.firstOrNull()?.discountTypes
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
            val filtered = categories.filter { it.isActive }
            return filtered
        } else {
            return emptyList()
        }
    }
}
