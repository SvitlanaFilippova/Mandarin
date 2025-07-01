package com.mandarinkafe.mandarin.features.menu.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.menu.domain.usecase.CategoryFilter
import jakarta.inject.Inject

class KeywordCategoryFilter @Inject constructor(
    private val keyword: String
) : CategoryFilter {
    override fun isMatch(category: MealCategory): Boolean {
        return category.name.contains(keyword, ignoreCase = true)
    }
}