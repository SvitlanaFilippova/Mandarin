package com.mandarinkafe.mandarin.features.favorites.domain.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.core.domain.api.FavoritesReader
import com.mandarinkafe.mandarin.core.domain.api.FavoritesWriter
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toFavoriteRecord
import com.mandarinkafe.mandarin.features.favorites.domain.usecase.ValidateFavoritesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesInteractorImpl(
    private val validator: ValidateFavoritesUseCase,
    private val reader: FavoritesReader,
    private val writer: FavoritesWriter,
) : FavoritesApi {
    private val _favoritesFlow = MutableStateFlow<List<CustomizedMeal>>(emptyList())
    override fun observeFavorites() = _favoritesFlow.asStateFlow()

    init {
        // При старте сразу грузим и логируем
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("FavoritesInteractor", "INIT: loading favorites…")
            updateFavoritesFlow()
        }
    }
    override suspend fun getFavorites(): List<CustomizedMeal> {
        return validator(reader.getRawFavorites()).toList().map { it }
    }

    override suspend fun checkIfFavorite(custom: CustomizedMeal): Boolean {
        return reader.checkIfFavorite(custom.toFavoriteRecord(getTimeStamp()))
    }

    override suspend fun checkIfFavorite(meal: Meal): Boolean {
        return reader.checkIfFavorite(meal.toFavoriteRecord(getTimeStamp()))
    }

    override suspend fun toggleFavorite(custom: CustomizedMeal): Boolean {
        val record = custom.toFavoriteRecord(getTimeStamp())
        Log.d("FavoritesInteractor", "toggleFavorite: record=$record")
        val wasAdded = writer.toggleFavorite(record)
        Log.d("FavoritesInteractor", "toggleFavorite: wasAdded=$wasAdded")
        updateFavoritesFlow()
        return wasAdded
    }

    override suspend fun toggleFavorite(meal: Meal): Boolean {
        val record = meal.toFavoriteRecord(getTimeStamp())
        Log.d("FavoritesInteractor", "toggleFavorite(meal): record=$record")
        val wasAdded = writer.toggleFavorite(record)
        Log.d("FavoritesInteractor", "toggleFavorite(meal): wasAdded=$wasAdded")
        updateFavoritesFlow()
        return wasAdded
    }

    private fun getTimeStamp(): Long {
        return System.currentTimeMillis()
    }

    private suspend fun updateFavoritesFlow() {
        // Читаем сырые
        val raw = reader.getRawFavorites()
        // Валидируем и  сортируем по по timestamp
        val validRecords = validator(raw)
        //  Пушим в StateFlow
        _favoritesFlow.value = validRecords
    }
}