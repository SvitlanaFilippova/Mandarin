package com.mandarinkafe.mandarin.favorites.domain.impl

import com.mandarinkafe.mandarin.favorites.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.favorites.domain.models.FavoriteMeal
import com.mandarinkafe.mandarin.favorites.domain.usecase.FavoritesInteractor

class FavoritesInteractorImpl(private val repository: FavoritesRepository): FavoritesInteractor {
    override fun getFavorites(): List<FavoriteMeal> = repository.getFavorites()

    override fun addToFavorites(meal: FavoriteMeal) {
        repository.addToFavorites(meal) }

    override fun removeFromFavorites(meal: FavoriteMeal) {
        repository.removeFromFavorites(meal)
    }

    override fun checkIfFavorite(itemId: String): Boolean {
        return repository.checkIfFavorite(itemId)
    }
}