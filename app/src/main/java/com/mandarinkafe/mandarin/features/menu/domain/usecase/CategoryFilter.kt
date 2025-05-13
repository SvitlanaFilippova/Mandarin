package com.mandarinkafe.mandarin.features.menu.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.MealCategory

interface CategoryFilter {
    fun isMatch(category: MealCategory): Boolean
}