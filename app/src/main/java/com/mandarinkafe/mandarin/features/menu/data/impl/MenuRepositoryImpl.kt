package com.mandarinkafe.mandarin.features.menu.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.api.MenuFetcher
import com.mandarinkafe.mandarin.core.data.network.NetworkClient
import com.mandarinkafe.mandarin.core.domain.api.FavoritesReader
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.menu.data.dto.CategoryDto
import com.mandarinkafe.mandarin.features.menu.data.dto.MenuResponse
import com.mandarinkafe.mandarin.features.menu.data.mapper.hasParent
import com.mandarinkafe.mandarin.features.menu.data.mapper.parentName
import com.mandarinkafe.mandarin.features.menu.data.mapper.subName
import com.mandarinkafe.mandarin.features.menu.data.mapper.toDomain
import com.mandarinkafe.mandarin.features.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.applyTypography
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class MenuRepositoryImpl @Inject constructor(
    private val networkClient: NetworkClient,
    private val favoritesReader: FavoritesReader
) : MenuRepository, MenuFetcher {

    override suspend fun fetchMenu(): Resource<List<MealCategory>> {
        return try {
            val response = networkClient.getMenu()

            when (response.resultCode) {
                NO_CONNECTION -> Resource.ErrorNoInternet()
                HTTP_SUCCESS -> {
                    val categories = (response as MenuResponse).itemCategories
                    if (!categories.isNullOrEmpty()) {
                        Resource.Success(buildMenuStructure(categories))
                    } else {
                        Resource.ErrorEmptyData()
                    }
                }

                else -> Resource.ErrorOther("Ошибка сервера или пустой ответ")
            }
        } catch (e: Exception) {
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    private suspend fun buildMenuStructure(menuDto: List<CategoryDto>?): List<MealCategory> {
        if (menuDto.isNullOrEmpty()) {
            Log.e("DEBUG", "menuDto оказался null или пустым")
            return emptyList()
        }
        val baseFavIds = favoritesReader.getBaseFavoritesIds()

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
                        storedFavorites = baseFavIds
                    )
                )
            } else {
                // Есть подкатегории — собрать как категорию с subCategories
                result.add(buildParentCategory(parent, subCategories, baseFavIds))
            }
        }

        // 2. Обработка случайных подкатегорий без родителя
        for (category in menuDto.filter { it.hasParent() }) {
            val parentName = category.parentName()
            if (!topLevelNames.contains(parentName)) {
                result.add(buildLonelySubcategory(category, baseFavIds))
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
        subCategories: List<CategoryDto>?,
        storedFavorites: Set<String>,
    ): MealCategory {
        val name = parentDto.name.applyTypography()
        return MealCategory(
            id = parentDto.id,
            name = name,
            meals = null,
            subCategories = subCategories?.map { subDto ->
                subDto.copy(name = subDto.subName()).toDomain(
                    storedFavorites = storedFavorites,
                    topCategoryName = name
                )
            },
            tabIcon = parentDto.buttonImageUrl,
            description = parentDto.description.orEmpty().applyTypography(),
            isHidden = parentDto.isHidden == true,
        )
    }

    private fun buildLonelySubcategory(
        category: CategoryDto,
        storedFavorites: Set<String>
    ): MealCategory {
        Log.w("DEBUG", "Подкатегория '${category.name}' без родителя")
        return category.copy(name = category.subName()).toDomain(storedFavorites)
    }

    private fun collectAllMeals(category: MealCategory, result: MutableList<Meal>) {
        // Добавляем блюда из этой категории
        category.meals?.let { result.addAll(it) }

        // Рекурсивно добавляем блюда из подкатегорий
        category.subCategories?.forEach { subCategory ->
            collectAllMeals(subCategory, result)
        }
    }
}