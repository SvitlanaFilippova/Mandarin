package com.mandarinkafe.mandarin.menu.data.dto

data class MenuResponse(
    val itemCategories: List<CategoryDto>?,
) : Response()

data class CategoryDto(
    val id: String,
    val name: String,
    val items: List<MealDto>,
    val buttonImageUrl: String?
)

data class MealDto(
    val itemId: String,
    val sku: String,
    val name: String,
    val description: String,
    val tags: List<Tag>,
    val itemSizes: List<ItemSize>
    )

data class Tag(
    val id: String,
    val name: String
)

data class ItemSize(
    val portionWeightGrams: Double ,
    val prices: List<Price>,
    val buttonImageUrl: String,
)

data class Price(
    val organizationId: String,
    val price: Double
)



