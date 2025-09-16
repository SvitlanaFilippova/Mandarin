package com.mandarinkafe.mandarin.features.favorites.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.FavoriteRecord
import com.mandarinkafe.mandarin.core.domain.models.id
import com.mandarinkafe.mandarin.features.cart.data.validateBy
import com.mandarinkafe.mandarin.features.favorites.domain.usecase.ValidateFavoritesUseCase
import com.mandarinkafe.mandarin.features.menu.domain.mappers.toMealAdditional
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.first

class ValidateFavoritesUseCaseImpl(
    private val menuCache: MenuCache,
) : ValidateFavoritesUseCase {

    override suspend fun invoke(raw: Set<FavoriteRecord>): Resource<List<CustomizedMeal>> {
        return try {
            // Ожидаем меню
            waitForMenu()

            val result = processRecords(raw)

            Resource.Success(
                result.validPairs
                    .sortedByDescending { it.first.timestamp }
                    .map { it.second }.distinctBy { it.id }
            )
        } catch (e: Exception) {
            Resource.ErrorOther(e.message ?: "Favorites validation error")
        }
    }

    private suspend fun waitForMenu() {
        menuCache.allVisibleMenu.first { it is Resource.Success }
    }

    private fun processRecords(
        raw: Set<FavoriteRecord>
    ): ValidationResult {
        val validPairs = mutableListOf<Pair<FavoriteRecord, CustomizedMeal>>()


        for (record in raw) {
            val fullMeal = menuCache.getMealById(record.mealId)
            if (fullMeal == null) {
                continue
            }

            when (record) {
                is FavoriteRecord.Base -> {
                    val customized = CustomizedMeal(
                        meal = fullMeal,
                        adds = emptyList(),
                        modifiers = emptyList()
                    )
                    validPairs += record to customized
                }

                is FavoriteRecord.Custom -> {
                    val validAdds = record.addsIds.mapNotNull { id ->
                        val additional = menuCache.getMealById(id)?.toMealAdditional()
                        additional
                    }

                    val validMods = record.modifiers.validateBy(fullMeal.modifiers)

                    val cleaned = record.copy(
                        addsIds = validAdds.map { it.id },
                        modifiers = validMods
                    )
                    val customized = CustomizedMeal(
                        meal = fullMeal,
                        adds = validAdds,
                        modifiers = validMods
                    )
                    validPairs += cleaned to customized
                }
            }
        }

        return ValidationResult(
            validPairs = validPairs,
        )
    }

    private data class ValidationResult(
        val validPairs: List<Pair<FavoriteRecord, CustomizedMeal>>,
    )
}
