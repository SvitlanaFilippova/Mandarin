package com.mandarinkafe.mandarin.features.favorites.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.core.domain.api.FavoritesReader
import com.mandarinkafe.mandarin.core.domain.api.FavoritesWriter
import com.mandarinkafe.mandarin.core.domain.api.ForceRefreshMenuUseCase
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val reader: FavoritesReader,
    private val writer: FavoritesWriter,
    private val forceRefreshMenu: ForceRefreshMenuUseCase,
) : FavoritesApi {

    override fun observeFavoritesItems(): Flow<Resource<List<CustomizedMeal>>> =
        reader.observeFavorites()

    override fun observeFavoritesBaseMealIDs(): Flow<Set<String>> =
        reader.observeBaseFavoritesIds()

    override suspend fun forceRefresh() {
        forceRefreshMenu()
        reader.forceRetry()
    }

    override suspend fun toggleFavorite(custom: CustomizedMeal) {
        writer.toggleFavorite(custom)
    }

    override suspend fun toggleFavorite(meal: Meal) {
        writer.toggleFavorite(meal)
    }

}
