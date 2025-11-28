package com.mandarinkafe.mandarin.features.infrastructure.domain.models

import com.mandarinkafe.mandarin.database.Category_discount

data class CategoryDiscountMap(
    val categoryId: String,
    val discountId: String,
)

fun Category_discount.toDomain() = CategoryDiscountMap(
    categoryId = categoryId,
    discountId = discountId
)