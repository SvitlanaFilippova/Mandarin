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
            tabIcon = buttonImageUrl
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
                imageUrl = itemSizes.firstOrNull()?.buttonImageUrl,
                categoryId = categoryId,
                isFavorite = storedFavorites.contains(itemId),
                tags = tags,
                topCategoryId = topCategoryId,
                isEditable = checkIfEditable(categoryId)

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


    private fun menuDtoToDomain(menuDto: List<CategoryDto>?, parentCategory: String?): List<MealCategory> {
        if (menuDto.isNullOrEmpty()) {
            Log.e("DEBUG", "menuDto оказался null или пустым")
            return emptyList()
        }
        val menuDomain = menuDto.map {
            it.toDomain(storedFavorites = storedFavorites, parentCategory)
        }
        return menuDomain
    }

    fun setMenuStructure(menuDto: List<CategoryDto>?): List<MealCategory> {
        if (menuDto.isNullOrEmpty()) {
            Log.e("DEBUG", "menuDto оказался null или пустым")
            return emptyList()
        }
        return buildList<MealCategory> {
            this.add(
                PIZZA_INDEX,
                MealCategory(
                    id = PARENT_PIZZA_ID,
                    name = PIZZA_PARENT_CATEGORY_NAME,
                    meals = null,
                    subCategories = menuDtoToDomain(menuDto, PARENT_PIZZA_ID)
                        .filter { category -> PizzaCategoriesIds.contains(category.id) }, // Потом применяем filter
                    tabIcon = PIZZA_ICON
                )
            )

            this.add(
                SUSHI_INDEX,
                MealCategory(
                    id = PARENT_SUSHI_ID,
                    name = SUSHI_PARENT_CATEGORY_NAME,
                    meals = null,
                    subCategories = menuDtoToDomain(menuDto, PARENT_SUSHI_ID)
                        .filter { category -> SushiCategoriesIds.contains(category.id) }, // Потом применяем filter
                    tabIcon = SUSHI_ICON
                )
            )
            this += menuDtoToDomain(menuDto, null).filter { category ->
                !SushiCategoriesIds.contains(category.id) && !PizzaCategoriesIds.contains(category.id)
            }
        }
    }


    enum class PizzaCategoriesIds(val id: String) {
        PIZZA35("832b4f72-adeb-4a3d-8bf4-cfde11ac810f"),

        //TODO выписать, когда будут в меню:
        RIM("9a9c0f12-123b-4d9f-8a34-cf1234abcd12"),
        NEAPOL("123b4a72-cdfg-7b3c-8abc-cfde11abc810"),
        CHICAGO("9a9c0f12-123b-4d9f-8a34-cf1234abcd12"),
        CALCONE("9a9c0f12-123b-4d9f-8a34-cf1234abcd12"),
        FOKACCHA("9a9c0f12-123b-4d9f-8a34-cf1234abcd12"),
        LAMADJO("9a9c0f12-123b-4d9f-8a34-cf1234abcd12"),
        HACHAPURI_ADJ("9a9c0f12-123b-4d9f-8a34-cf1234abcd12"),
        HACHAPURI_IM("9a9c0f12-123b-4d9f-8a34-cf1234abcd12"),
        RULETIKI("9a9c0f12-123b-4d9f-8a34-cf1234abcd12");

        companion object {
            private val ids = entries.map { it.id }.toSet()
            fun contains(id: String): Boolean {
                return id in ids
            }

        }
    }

    enum class SushiCategoriesIds(val id: String) {
        ROLLS("d3872541-5a16-4c21-b9e7-c8ab8912fd36"),

        //TODO выписать, когда будут в меню:
        MAKI("9a9c0f12-123b-4d9f-8a34-cf1234abcd12"),
        NIGIRI("123b4a72-cdfg-7b3c-8abc-cfde11abc810"),
        GUNKAN("9a9c0f12-123b-4d9f-8a34-cf1234abcd12"),
        SPICE("9a9c0f12-123b-4d9f-8a34-cf1234abcd12"),
        BAKED("9a9c0f12-123b-4d9f-8a34-cf1234abcd12"),
        HOT("9a9c0f12-123b-4d9f-8a34-cf1234abcd12"),
        ONIGIRI("9a9c0f12-123b-4d9f-8a34-cf1234abcd12"),
        SETS("9a9c0f12-123b-4d9f-8a34-cf1234abcd12");

        companion object {
            private val ids = entries.map { it.id }.toSet()
            fun contains(id: String): Boolean {
                return id in ids
            }
        }
    }

    companion object {
        const val PIZZA_ICON =
            "https://static.tildacdn.com/tild3061-3031-4532-a465-613433653562/noroot.png"
        const val SUSHI_ICON =
            "https://static.tildacdn.com/tild6163-3565-4364-b832-646639656462/noroot.png"
        const val PIZZA_INDEX = 0
        const val SUSHI_INDEX = 1
        const val PIZZA_PARENT_CATEGORY_NAME = "Пицца"
        const val SUSHI_PARENT_CATEGORY_NAME = "Суши и роллы"
        const val PARENT_PIZZA_ID = "parent_pizza_category_id"
        const val PARENT_SUSHI_ID = "parent_sushi_category_id"
    }
}






