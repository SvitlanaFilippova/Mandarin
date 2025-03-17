package com.mandarinkafe.mandarin.menu.domain.impl

import com.mandarinkafe.mandarin.menu.domain.api.FavoritesInteractor
import com.mandarinkafe.mandarin.menu.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.menu.domain.models.Meal

class FavoritesInteractorImpl(private val repository: FavoritesRepository): FavoritesInteractor {
    override fun addToFavorites(meal: Meal) {
        repository.addToFavorites(meal) }

    override fun removeFromFavorites(meal: Meal) {
        repository.removeFromFavorites(meal)
    }

    override fun checkIfFavorite(itemId: String): Boolean {
        return repository.checkIfFavorite(itemId)
    }
}