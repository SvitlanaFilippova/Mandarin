package com.mandarinkafe.mandarin.features.favorites.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.core.domain.api.FavoritesReader
import com.mandarinkafe.mandarin.core.domain.api.FavoritesWriter
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toFavoriteRecord
import com.mandarinkafe.mandarin.features.favorites.domain.usecase.ValidateFavoritesUseCase

class FavoritesInteractorImpl(
    private val validateFavoritesUseCase: ValidateFavoritesUseCase,
    private val reader: FavoritesReader,
    private val writer: FavoritesWriter,
) : FavoritesApi {

    override suspend fun getFavorites(): List<CustomizedMeal> {
        return validateFavoritesUseCase(reader.getRawFavorites()).toList().map { it }
    }

    override suspend fun checkIfFavorite(custom: CustomizedMeal): Boolean {
        return reader.checkIfFavorite(custom.toFavoriteRecord(getTimeStamp()))
    }

    override suspend fun checkIfFavorite(meal: Meal): Boolean {
        return reader.checkIfFavorite(meal.toFavoriteRecord(getTimeStamp()))
    }

    override suspend fun toggleFavorite(custom: CustomizedMeal): Boolean {
        return writer.toggleFavorite(custom.toFavoriteRecord(getTimeStamp()))
    }

    override suspend fun toggleFavorite(meal: Meal): Boolean {
        return writer.toggleFavorite(meal.toFavoriteRecord(getTimeStamp()))
    }

    private fun getTimeStamp(): Long {
        return System.currentTimeMillis()
    }
}