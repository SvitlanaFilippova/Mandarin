package com.mandarinkafe.mandarin.features.favorites.data.impl

import com.mandarinkafe.mandarin.core.domain.api.FavoritesReader
import com.mandarinkafe.mandarin.core.domain.api.FavoritesWriter
import com.mandarinkafe.mandarin.core.domain.models.FavoriteRecord
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toFavoriteRecords
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toStored
import com.mandarinkafe.mandarin.features.favorites.data.models.StoredFavoriteMeal
import com.mandarinkafe.mandarin.features.favorites.data.sharedprefs.FavoritesStorage

class FavoritesRepositoryImpl(
    private val storage: FavoritesStorage,
) : FavoritesReader, FavoritesWriter {

    override suspend fun getRawFavorites(): Set<FavoriteRecord> {
        val rawStored: Set<StoredFavoriteMeal> = storage.getFavorites()
        return rawStored.toFavoriteRecords()
    }

    override suspend fun getBaseFavoritesIds(): Set<String> {
        return getRawFavorites().filterIsInstance<FavoriteRecord.Base>().map { it.mealId }.toSet()
    }

    override suspend fun checkIfFavorite(item: FavoriteRecord): Boolean {
        val rawFavorites = storage.getFavorites()
        return rawFavorites.contains(item.toStored())
    }

    override suspend fun toggleFavorite(record: FavoriteRecord): Boolean {
        return storage.toggleFavorite(record.toStored())
    }

    override suspend fun saveFavorites(records: Set<FavoriteRecord>) {
        val dtos = records.map { it.toStored() }.toSet()
        storage.saveFavorites(dtos)
    }
}