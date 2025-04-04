package com.mandarinkafe.mandarin.menu.data

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

        val topLevelSet = mutableSetOf<String>()
        val processedParents = mutableSetOf<String>()
        val childCategoriesMap = mutableMapOf<String, MutableList<CategoryDto>>()

        // Группируем дочерние категории по имени родителя
        for (category in menuDto) {
            val split = category.name.split("/")
            if (split.size > 1) {
                val parentName = split.first()
                childCategoriesMap.getOrPut(parentName) { mutableListOf() }.add(category)
            } else {
                topLevelSet.add(category.name)
            }
        }

        return buildList {
            for (category in menuDto) {
                val split = category.name.split("/")
                if (split.size == 1) {
                    // Родительская категория
                    val parentName = category.name
                    if (processedParents.contains(parentName)) continue

                    val subCategoriesDto = childCategoriesMap[parentName]
                    processedParents.add(parentName)

                    if (subCategoriesDto != null) {
                        add(
                            MealCategory(
                                id = category.id,
                                name = parentName,
                                meals = null,
                                subCategories = subCategoriesDto.map { subDto ->
                                    val cleanedName = subDto.name.substringAfter("/")
                                    subDto.copy(name = cleanedName)
                                        .toDomain(
                                            storedFavorites = storedFavorites,
                                            parentCategory = category.id
                                        )
                                },
                                tabIcon = category.buttonImageUrl,
                                description = category.description ?: "",
                                isHidden = category.isHidden
                            )
                        )
                    } else {
                        add(
                            category.toDomain(
                                storedFavorites = storedFavorites,
                                parentCategory = null
                            )
                        )
                    }
                } else {
                    // Дочерние категории обрабатываются только в составе родителя
                    continue
                }
            }
        }
    }

    enum class PizzaCategoriesIds(val id: String) {
        PIZZA35("832b4f72-adeb-4a3d-8bf4-cfde11ac810f"),
        //TODO выписать, когда будут в меню:
        RIM("9a9c0f12-123b-4d9f-8a34-cf1234abcd12"),
    }
}