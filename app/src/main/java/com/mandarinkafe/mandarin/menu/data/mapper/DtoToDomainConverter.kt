package com.mandarinkafe.mandarin.menu.data.mapper

import android.util.Log
import com.mandarinkafe.mandarin.menu.data.dto.CategoryDto
import com.mandarinkafe.mandarin.menu.data.dto.MealDto
import com.mandarinkafe.mandarin.menu.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MealCategory

class DtoToDomainConverter(favoritesRepository: FavoritesRepository) {
    private val storedFavorites = favoritesRepository.getFavoriteIds()
    private val editableCategoryIds = setOf(
        PizzaCategoriesIds.PIZZA35.id,
        PizzaCategoriesIds.RIM.id,
    )

    private fun CategoryDto.toDomain(storedFavorites: List<String>, parentCategory: String?) =
        MealCategory(
            id = id,
            name = name,
            meals = items.mapNotNull {
                it.toDomain(
                    storedFavorites = storedFavorites,
                    categoryId = id,
                    topCategoryId = parentCategory ?: id
                )
            },
            subCategories = null,
            tabIcon = buttonImageUrl,
            description = description ?: "",
            isHidden = isHidden,
        )

    private fun MealDto.toDomain(
        storedFavorites: List<String>,
        categoryId: String,
        topCategoryId: String
    ): Meal? {
        try {
            val item = Meal(
                id = itemId,
                sku = sku,
                name = name,
                description = description,
                weight = itemSizes.firstOrNull()?.portionWeightGrams?.toInt() ?: 0,
                price = itemSizes.firstOrNull()?.prices?.firstOrNull()?.price?.toInt() ?: 0,
                imageUrl = itemSizes.firstOrNull()?.buttonImageUrl ?: "",
                categoryId = categoryId,
                isFavorite = storedFavorites.contains(itemId),
                tags = tags,
                topCategoryId = topCategoryId,
                isEditable = checkIfEditable(categoryId),
                isHidden = isHidden,
            )
            return item
        } catch (e: Throwable) {
            Log.d("DEBUG", "Error in fun ItemDto.toDomain. ${e.message}")
        }
        return null
    }

    private fun checkIfEditable(categoryId: String): Boolean {
        return editableCategoryIds.contains(categoryId)
    }

    fun menuDtoToDomain(menuDto: List<CategoryDto>?): List<MealCategory> {
        if (menuDto.isNullOrEmpty()) {
            Log.e("DEBUG", "menuDto оказался null или пустым")
            return emptyList()
        }

        val childCategoriesMap = groupSubcategories(menuDto)
        val topLevelSet = menuDto
            .filter { !it.name.contains("/") }
            .mapTo(mutableSetOf()) { it.name }

        val processedParents = mutableSetOf<String>()

        return buildList {
            for (category in menuDto) {
                val split = category.name.split("/")

                if (split.size == 1) {
                    // Родительская категория
                    val parentName = category.name
                    if (processedParents.add(parentName)) {
                        add(buildParentCategory(category, childCategoriesMap[parentName]))
                    }
                } else {
                    // Подкатегория без родителя
                    val parentName = split.first()
                    if (!topLevelSet.contains(parentName)) {
                        add(buildLonelySubcategory(category))
                    }
                }
            }
        }
    }

    private fun groupSubcategories(menuDto: List<CategoryDto>): Map<String, List<CategoryDto>> {
        return menuDto
            .filter { it.hasParent() }
            .groupBy { it.parentName() }
    }

    private fun buildParentCategory(
        parentDto: CategoryDto,
        subCategories: List<CategoryDto>?
    ): MealCategory {
        return MealCategory(
            id = parentDto.id,
            name = parentDto.name,
            meals = null,
            subCategories = subCategories?.map { subDto ->
                subDto.copy(name = subDto.subName()).toDomain(
                    storedFavorites = storedFavorites,
                    parentCategory = parentDto.id
                )
            },
            tabIcon = parentDto.buttonImageUrl,
            description = parentDto.description.orEmpty(),
            isHidden = parentDto.isHidden
        )
    }

    private fun buildLonelySubcategory(category: CategoryDto): MealCategory {
        Log.w("DEBUG", "Подкатегория '${category.name}' без родителя")
        return category.copy(name = category.subName()).toDomain(
            storedFavorites = storedFavorites,
            parentCategory = null
        )
    }

    enum class PizzaCategoriesIds(val id: String) {
        PIZZA35("832b4f72-adeb-4a3d-8bf4-cfde11ac810f"),
        RIM("9a9c0f12-123b-4d9f-8a34-cf1234abcd12"),
    }
}