package com.mandarinkafe.mandarin.menu.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.MealCategory

interface CategoryFilter {
    fun isMatch(category: MealCategory): Boolean
}