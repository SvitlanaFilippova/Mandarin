package com.mandarinkafe.mandarin.features.favorites.domain.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.domain.api.FavoritesWriter
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.FavoriteRecord
import com.mandarinkafe.mandarin.features.cart.validateBy
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
            // Превращаем raw → DTO для сравнения
            val rawStored = raw.map { it.toStored() }.toSet()

            // Временные коллекции
            val validPairs = mutableListOf<Pair<FavoriteRecord, CustomizedMeal>>()
            val validStored = mutableSetOf<FavoriteRecord>()
            val invalidIds = mutableListOf<String>()

            // Ждём, пока меню загрузится
            menuCache.menu.first { it is Resource.Success }

            for (record in raw) {
                val fullMeal = menuCache.getMealById(record.mealId)
                if (fullMeal == null) {
                    invalidIds += record.mealId
                    continue
                }

                when (record) {
                    is FavoriteRecord.Base -> {
                        // Базовый случай
                        val customized = CustomizedMeal(
                            meal = fullMeal,
                            adds = emptyList(),
                            modifiers = emptyList()
                        )
                        validPairs += record to customized
                        validStored += record
                    }

                    is FavoriteRecord.Custom -> {
                        // Валидируем adds
                        val validAdds = record.addsIds
                            .mapNotNull { addId ->
                                menuCache.getMealById(addId)
                                    ?.toMealAdditional()
                            }
                        // Валидируем modifiers
                        val validMods = record.modifiers
                            .validateBy(fullMeal.modifiers)

                        // Собираем очищенную запись
                        val cleanedRecord = record.copy(
                            addsIds = validAdds.map { it.id },
                            modifiers = validMods
                        )
                        // Собираем CustomizedMeal
                        val customized = CustomizedMeal(
                            meal = fullMeal,
                            adds = validAdds,
                            modifiers = validMods
                        )
                        validPairs += cleanedRecord to customized
                        validStored += cleanedRecord
                    }
                }
            }

            // Если что-то подчистилось — сохраняем «чистый» набор
            val newStoredDtos = validStored.map { it.toStored() }.toSet()
            if (newStoredDtos != rawStored) {
                writer.saveFavorites(validStored)
                Log.d("Favorites", "Removed invalid entries: $invalidIds")
            }

            // Сортируем пары по timestamp и возвращаем только CustomizedMeal
            Resource.Success(
                validPairs
                    .sortedByDescending { it.first.timestamp }
                    .map { it.second })
        } catch (e: Exception) {
            Resource.ErrorOther(e.message ?: "Favorites validation error")
        }
    }
}

