package com.mandarinkafe.mandarin.features.favorites.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.api.FavoritesReader
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.features.cart.validateBy
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toFavoriteRecords
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toStoredFavoriteMeal
import com.mandarinkafe.mandarin.features.favorites.data.models.StoredFavoriteMeal
import com.mandarinkafe.mandarin.features.favorites.data.sharedprefs.FavoritesStorage
import com.mandarinkafe.mandarin.features.favorites.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.features.favorites.domain.models.FavoriteRecord
import com.mandarinkafe.mandarin.features.menu.domain.mappers.toMealAdditional
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.first

class FavoritesRepositoryImpl(
    private val storage: FavoritesStorage,
    private val menuCache: MenuCache,
) :
    FavoritesRepository, FavoritesReader {

    override suspend fun checkIfFavorite(item: FavoriteRecord): Boolean {
        val rawFavorites = storage.getFavorites()
        return rawFavorites.contains(item.toStoredFavoriteMeal())
    }

    override suspend fun toggleFavorite(item: FavoriteRecord): Boolean {
        return storage.toggleFavorite(item.toStoredFavoriteMeal())
    }

    override suspend fun getFavorites(): Set<FavoriteRecord> {
        // 1. Считаем «сырые» данные из хранилища
        val rawStored: Set<StoredFavoriteMeal> = storage.getFavorites()
        // 2. Мапим их в FavoriteRecord (Base или Custom)
        val rawRecords: Set<FavoriteRecord> = rawStored.toFavoriteRecords()

        // сюда будем складывать только действительно валидные записи
        val validRecords = mutableSetOf<FavoriteRecord>()
        // и одновременно пересоберём DTO-набор для storage
        val validStored = mutableSetOf<StoredFavoriteMeal>()
        // для фильтрации несуществующих блюд
        val invalidMealIds = mutableSetOf<String>()

        // 3. Ждём, пока меню загрузится
        menuCache.menu.first { it is Resource.Success }

        for (record in rawRecords) {
            // 4. Проверяем, что базовое блюдо есть в актуальном меню
            val fullMeal = menuCache.getMealById(record.mealId)
            if (fullMeal == null) {
                invalidMealIds += record.mealId
                continue
            }

            when (record) {
                is FavoriteRecord.Base -> {
                    // если блюдо есть — принимаем Base
                    validRecords += record
                    validStored += StoredFavoriteMeal(
                        mealId = record.mealId,
                        timestamp = record.timestamp,
                        addsIds = emptyList(),
                        modifiers = emptyList(),
                    )
                }

                is FavoriteRecord.Custom -> {
                    // валидируем добавки
                    val validAdds = record.addsIds
                        .mapNotNull { addId ->
                            // ищем дополнительное блюдо в меню (может быть другой репозиторий, здесь – пример)
                            menuCache.getMealById(addId)?.toMealAdditional()
                        }
                    // валидируем группы модификаторов (например, пропуская несуществующие)
                    val validModifiers = record.modifiers.validateBy(fullMeal.modifiers)

                    // формируем новый Custom record
                    val cleanedCustom = FavoriteRecord.Custom(
                        mealId = record.mealId,
                        timestamp = record.timestamp,
                        addsIds = validAdds.map { it.id },
                        modifiers = validModifiers
                    )
                    validRecords += cleanedCustom

                    // сохраняем в таком же виде в хранилище
                    validStored += StoredFavoriteMeal(
                        mealId = record.mealId,
                        timestamp = record.timestamp,
                        addsIds = cleanedCustom.addsIds,
                        modifiers = cleanedCustom.modifiers
                    )
                }
            }
        }

        // 5. Если были удалены невалидные или подчистились кастомные, сохраняем «чистый» сет
        if (validStored.size != rawStored.size) {
            storage.saveFavorites(validStored)
            Log.d("Favorites", "Сброшены невалидные/устаревшие избранные: $invalidMealIds")
        }

        // 6. Возвращаем уже отфильтрованный и очищенный набор FavoriteRecord
        return validRecords
    }

    override suspend fun getBaseFavoritesIds(): Set<String> {
        return getFavorites().filterIsInstance<FavoriteRecord.Base>().map { it.mealId }.toSet()
    }
}