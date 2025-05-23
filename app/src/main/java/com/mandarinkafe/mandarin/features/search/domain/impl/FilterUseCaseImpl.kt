package com.mandarinkafe.mandarin.features.search.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.search.domain.usecase.FilterUseCase
import com.mandarinkafe.mandarin.features.search.fuzzyContains
import com.mandarinkafe.mandarin.features.search.levenshteinDistance
import com.mandarinkafe.mandarin.features.search.toTranslitVariants

class FilterUseCaseImpl : FilterUseCase {

    override fun invoke(
        meals: List<Meal>,
        searchText: String,
        checkedLabels: List<String>
    ): List<Meal> {
        val searchVariants = searchText.toTranslitVariants()

        return meals
            .filter { meal ->
                val matchesText = listOf(meal.name, meal.parentCategoryName).any { field ->
                    searchVariants.any { variant -> field.fuzzyContains(variant) }
                }

                val labelNames = meal.labels.map { it.name }
                val matchesLabels =
                    checkedLabels.isEmpty() || checkedLabels.all { it in labelNames }

                matchesText && matchesLabels
            }
            .sortedWith(
                compareBy<Meal> { meal ->
                    val distances = searchVariants.flatMap { variant ->
                        listOf(
                            meal.name.levenshteinDistance(variant),
                            meal.parentCategoryName.levenshteinDistance(variant)
                        )
                    }
                    distances.minOrNull() ?: Int.MAX_VALUE
                }.thenByDescending { it.isFavorite }
            )
    }
}
