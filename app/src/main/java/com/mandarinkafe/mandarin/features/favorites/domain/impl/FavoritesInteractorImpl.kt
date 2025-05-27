package com.mandarinkafe.mandarin.features.favorites.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.features.favorites.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.features.favorites.domain.models.FavoriteMeal
import com.mandarinkafe.mandarin.features.favorites.domain.usecase.FavoritesInteractor

class FavoritesInteractorImpl(private val repository: FavoritesRepository) : FavoritesInteractor,
    FavoritesApi {
    override suspend fun getFavorites(): List<FavoriteMeal> = repository.getFavorites()

    override suspend fun addFavorite(meal: FavoriteMeal) {
        repository.addFavorite(meal)
    }

    override suspend fun removeFavorite(meal: FavoriteMeal) {
        repository.removeFavorite(meal)
    }

    override suspend fun checkIfFavorite(itemId: String): Boolean {
        return repository.checkIfFavorite(itemId)
    }

}