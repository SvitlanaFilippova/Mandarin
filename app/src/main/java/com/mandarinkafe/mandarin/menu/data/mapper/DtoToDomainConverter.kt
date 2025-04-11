package com.mandarinkafe.mandarin.menu.data.mapper

import android.util.Log
import com.mandarinkafe.mandarin.favorites.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.menu.data.dto.CategoryDto
import com.mandarinkafe.mandarin.menu.data.dto.MealDto
import com.mandarinkafe.mandarin.menu.data.dto.TagDto
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MealCategory
import com.mandarinkafe.mandarin.menu.domain.models.Tag
import com.mandarinkafe.mandarin.util.applyTypography

class DtoToDomainConverter(favoritesRepository: FavoritesRepository) {
    private val storedFavorites = favoritesRepository.getFavoriteIds()
    private val editableCategoryIds = setOf(
        PizzaCategoriesIds.PIZZA35.id,
        PizzaCategoriesIds.RIM.id,
    )

    private fun CategoryDto.toDomain(storedFavorites: List<String>, parentCategory: String?) =
        MealCategory(
            id = id,
            name = name.applyTypography(),
            meals = items.mapNotNull {
                it.toDomain(
                    storedFavorites = storedFavorites,
                    categoryId = id,
                    topCategoryId = parentCategory ?: id
                )
            },
            subCategories = null,
            tabIcon = buttonImageUrl,
            description = (description ?: "").applyTypography(),
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
                name = name.applyTypography(),
                description = (description ?: "").applyTypography(),
                weight = itemSizes.firstOrNull()?.portionWeightGrams?.toInt() ?: 0,
                price = itemSizes.firstOrNull()?.prices?.firstOrNull()?.price?.toInt() ?: 0,
                imageUrl = itemSizes.firstOrNull()?.buttonImageUrl ?: "",
                isFavorite = storedFavorites.contains(itemId),
                tags = (tags ?: emptyList()).map { it.toDomain() },
                isEditable = checkIfEditable(categoryId),
                isHidden = isHidden,
            )
            return item
        } catch (e: Throwable) {
            Log.d("DEBUG", "Error in fun ItemDto.toDomain. ${e.message}")
        }
        return null
    }

    private fun TagDto.toDomain() = Tag(
        id = id,
        name = name
    )

    private fun checkIfEditable(categoryId: String): Boolean {
        return editableCategoryIds.contains(categoryId)
    }

    fun menuDtoToDomain(menuDto: List<CategoryDto>?): List<MealCategory> {
        if (menuDto.isNullOrEmpty()) {
            Log.e("DEBUG", "menuDto оказался null или пустым")
            return emptyList()
        }

        val childCategoriesMap = groupSubcategories(menuDto)
        val topLevelCategories = menuDto.filter { !it.hasParent() }
        val topLevelNames = topLevelCategories.map { it.name }.toSet()

        val result = mutableListOf<MealCategory>()

        // 1. Обработка родительских категорий
        for (parent in topLevelCategories) {
            val subCategories = childCategoriesMap[parent.name]

            if (subCategories.isNullOrEmpty()) {
                // Нет подкатегорий — обычная категория с блюдами
                result.add(
                    parent.toDomain(
                        storedFavorites = storedFavorites,
                        parentCategory = null
                    )
                )
            } else {
                // Есть подкатегории — собрать как категорию с subCategories
                result.add(buildParentCategory(parent, subCategories))
            }
        }

        // 2. Обработка случайных подкатегорий без родителя
        for (category in menuDto.filter { it.hasParent() }) {
            val parentName = category.parentName()
            if (!topLevelNames.contains(parentName)) {
                result.add(buildLonelySubcategory(category))
            }
        }

        return result
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