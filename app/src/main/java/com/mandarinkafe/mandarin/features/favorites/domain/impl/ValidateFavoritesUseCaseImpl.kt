package com.mandarinkafe.mandarin.features.favorites.domain.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.domain.api.FavoritesWriter
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.FavoriteRecord
import com.mandarinkafe.mandarin.features.cart.data.validateBy
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toStored
import com.mandarinkafe.mandarin.features.favorites.domain.usecase.ValidateFavoritesUseCase
import com.mandarinkafe.mandarin.features.menu.domain.mappers.toMealAdditional
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.first

class ValidateFavoritesUseCaseImpl(
    private val menuCache: MenuCache,
    private val writer: FavoritesWriter
) : ValidateFavoritesUseCase {

    override suspend fun invoke(raw: Set<FavoriteRecord>): Resource<List<CustomizedMeal>> {
        return try {
            val rawStored = raw.map { it.toStored() }.toSet()

            // Ожидаем меню
            waitForMenu()

            val result = processRecords(raw)

            if (result.cleanedStored.map { it.toStored() }.toSet() != rawStored) {
                writer.saveFavorites(result.cleanedStored)
                Log.d("Favorites", "Removed invalid entries: ${result.invalidIds}")
            }

            Resource.Success(
                result.validPairs
                    .sortedByDescending { it.first.timestamp }
                    .map { it.second }
            )
        } catch (e: Exception) {
            Resource.ErrorOther(e.message ?: "Favorites validation error")
        }
    }

    private suspend fun waitForMenu() {
        menuCache.menu.first { it is Resource.Success }
    }

    private fun processRecords(
        raw: Set<FavoriteRecord>
    ): ValidationResult {
        val validPairs = mutableListOf<Pair<FavoriteRecord, CustomizedMeal>>()
        val cleanedStored = mutableSetOf<FavoriteRecord>()
        val invalidIds = mutableListOf<String>()

        for (record in raw) {
            val fullMeal = menuCache.getMealById(record.mealId)
            if (fullMeal == null) {
                invalidIds += record.mealId
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
                    cleanedStored += record
                }

                is FavoriteRecord.Custom -> {
                    val validAdds = record.addsIds.mapNotNull { id ->
                        menuCache.getMealById(id)?.toMealAdditional()
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
                    cleanedStored += cleaned
                }
            }
        }

        return ValidationResult(
            validPairs = validPairs,
            cleanedStored = cleanedStored,
            invalidIds = invalidIds
        )
    }

    private data class ValidationResult(
        val validPairs: List<Pair<FavoriteRecord, CustomizedMeal>>,
        val cleanedStored: Set<FavoriteRecord>,
        val invalidIds: List<String>
    )
}
