package com.mandarinkafe.mandarin.features.mealdetails.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.mealdetails.domain.usecase.GetAddonsUseCase
import com.mandarinkafe.mandarin.features.menu.domain.mappers.toMealAdditionalCategory
import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.util.Constants.CATEGORY_ADDS
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAddonsUseCaseImpl(
    private val cache: MenuCache,
) : GetAddonsUseCase {

    override fun invoke(categoryPath: List<String>): Flow<Resource<List<MealAdditionalCategory>>> {
        return cache.addonsCategories.map { addons ->
            val matched = addons.firstOrNull { it.categoryPath == categoryPath + CATEGORY_ADDS }
            Success(
                matched?.subCategories.orEmpty()
                    .map { it.toMealAdditionalCategory() }
            )
        }
    }

    private fun findCategoryByPath(
        path: List<String>,
        categories: List<MealCategory>
    ): MealCategory? {
        var currentLevel = categories
        var result: MealCategory? = null
        for (name in path) {
            result = currentLevel.find { it.name == name } ?: return null
            currentLevel = result.subCategories.orEmpty()
        }
        return result
    }
}