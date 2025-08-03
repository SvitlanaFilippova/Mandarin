package com.mandarinkafe.mandarin.features.mealdetails.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.features.mealdetails.domain.usecase.GetAddonsUseCase
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
            val baseCategory = categoryPath.firstOrNull()
            if (baseCategory == null) {
                return@map Success(emptyList())
            }

            val addonsPrefix = listOf(baseCategory, CATEGORY_ADDS)

            val total = addons
                .filter { it.categoryPath.startsWith(addonsPrefix) }
                .filter {
                    val depth = it.categoryPath.size
                    depth == addonsPrefix.size + 1 || it.categoryPath == addonsPrefix
                }
                .filter { !it.items.isNullOrEmpty() }

            Success(total)
        }
    }

    private fun List<String>.startsWith(prefix: List<String>): Boolean {
        if (this.size < prefix.size) return false
        return this.subList(0, prefix.size) == prefix
    }
}