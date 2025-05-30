package com.mandarinkafe.mandarin.features.search.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.Meal

interface FilterUseCase {
    operator fun invoke(
        meals: List<Meal>,
        searchText: String,
        checkedLabels: List<String>,
        favoritesIds: Set<String>
    ): List<Meal>
}