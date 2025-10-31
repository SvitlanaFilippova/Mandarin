package com.mandarinkafe.mandarin.features.search.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.isFavorite
import com.mandarinkafe.mandarin.features.search.domain.api.FilterUseCase
import com.mandarinkafe.mandarin.util.fuzzyContains
import com.mandarinkafe.mandarin.util.levenshteinDistance
import com.mandarinkafe.mandarin.util.toTranslitVariants

class FilterUseCaseImpl : FilterUseCase {
    override fun invoke(
        meals: List<Meal>,
        searchText: String,
        checkedLabels: List<String>,
        favoritesIds: Set<String>,
    ): List<Meal> {
        if (searchText.isBlank()) {
            return meals.filter { meal ->
                val labelNames = meal.labels.map { it.name }
                checkedLabels.isEmpty() || checkedLabels.all { it in labelNames }
            }.sortedByDescending { it.isFavorite(favoritesIds) }
        }

        val searchVariants = searchText.toTranslitVariants()

        return meals
            .filter { meal ->
                val searchableFields = listOf(
                    meal.name,
                    meal.categoryPath.joinToString(" ")
                )

                val matchesText = searchableFields.any { field ->
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
                            meal.name.levenshteinDistance(variant)
                        ) + meal.categoryPath.map { categorySegment ->
                            categorySegment.levenshteinDistance(variant)
                        }
                    }
                    distances.minOrNull() ?: Int.MAX_VALUE
                }.thenByDescending { it.isFavorite(favoritesIds) }
            )
    }
}