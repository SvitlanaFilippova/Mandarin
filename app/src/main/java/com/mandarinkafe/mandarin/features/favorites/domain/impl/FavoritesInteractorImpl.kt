package com.mandarinkafe.mandarin.features.favorites.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toFavoriteRecord
import com.mandarinkafe.mandarin.features.favorites.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.features.favorites.domain.models.FavoriteRecord
import com.mandarinkafe.mandarin.features.favorites.domain.usecase.FavoritesInteractor

class FavoritesInteractorImpl(private val repository: FavoritesRepository) : FavoritesInteractor,
    FavoritesApi {
    override suspend fun getFavorites(): List<FavoriteRecord> {
        return repository.getFavorites().toList()
    }

    override suspend fun toggleFavorite(meal: CustomizedMeal): Boolean {
        return repository.toggleFavorite(meal.toFavoriteRecord(timestamp = getTimeStamp()))
    }

    override suspend fun toggleFavorite(id: String): Boolean {
        return repository.toggleFavorite(
            FavoriteRecord.Base(
                mealId = id,
                timestamp = getTimeStamp()
            )
        )
    }

    override suspend fun checkIfFavorite(meal: CustomizedMeal): Boolean {
        return repository.checkIfFavorite(meal.toFavoriteRecord(timestamp = getTimeStamp()))
    }

    private fun getTimeStamp() = System.currentTimeMillis()
}