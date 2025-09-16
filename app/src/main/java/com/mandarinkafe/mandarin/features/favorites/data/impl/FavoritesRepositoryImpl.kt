package com.mandarinkafe.mandarin.features.favorites.data.impl

import com.mandarinkafe.mandarin.core.domain.api.FavoritesReader
import com.mandarinkafe.mandarin.core.domain.api.FavoritesWriter
import com.mandarinkafe.mandarin.core.domain.models.FavoriteRecord
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toFavoriteRecords
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toStored
import com.mandarinkafe.mandarin.features.favorites.data.sharedprefs.FavoritesStorage
import com.mandarinkafe.mandarin.features.favorites.data.sharedprefs.FavoritesStorageResult
import com.mandarinkafe.mandarin.util.NetworkMonitor
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorEmptyData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesRepositoryImpl(
    private val networkMonitor: NetworkMonitor,
    private val storage: FavoritesStorage,
) : FavoritesReader, FavoritesWriter {

    private val _favoritesFlow = MutableStateFlow<Resource<Set<FavoriteRecord>>>(ErrorEmptyData())
    private val _baseIdsFlow = MutableStateFlow<Set<String>>(emptySet())

    init {
        CoroutineScope(Dispatchers.IO).launch {
            refreshFavorites()
        }
    }

    override fun observeRawFavorites(): Flow<Resource<Set<FavoriteRecord>>> =
        _favoritesFlow.asStateFlow()

    override fun observeBaseFavoritesIds(): Flow<Set<String>> = _baseIdsFlow.asStateFlow()

    override suspend fun getRawFavorites(): Resource<Set<FavoriteRecord>> {
        return if (!isConnected()) {
            Resource.ErrorNoInternet()
        } else {
            _favoritesFlow.value
        }
    }

    override fun getBaseFavoritesIds(): Set<String> {
        return _baseIdsFlow.value
    }

    override suspend fun toggleFavorite(record: FavoriteRecord) {
        val currentSet = when (val current = _favoritesFlow.value) {
            is Resource.Success -> current.data?.toMutableSet() ?: mutableSetOf()
            else -> mutableSetOf()
        }
        if (currentSet.contains(record)) {
            currentSet.remove(record)
        } else {
            currentSet.add(record)
        }

        // Сохраняем изменения
        saveToStorage(currentSet)
    }

    override suspend fun saveFavorites(records: Set<FavoriteRecord>) {
        saveToStorage(records)
    }

    private suspend fun saveToStorage(records: Set<FavoriteRecord>) {
        val dtos = records.map { it.toStored() }.toSet()
        storage.saveFavorites(dtos)
        refreshFavorites()
    }

    private suspend fun refreshFavorites() {
        when (val stored = storage.getFavorites()) {
            is FavoritesStorageResult.Success -> {
                val records = stored.favorites.toFavoriteRecords()
                _favoritesFlow.value = if (isConnected()) {
                    Resource.Success(records)
                } else {
                    Resource.ErrorNoInternet()
                }

                _baseIdsFlow.value = records
                    .filterIsInstance<FavoriteRecord.Base>()
                    .map { it.mealId }
                    .toSet()
            }

            is FavoritesStorageResult.Corrupted -> {
                _favoritesFlow.value =
                    Resource.ErrorOther("Произошла критическая ошибка при попытке получения избранных. Пришлось их обнулить :( ")
                _baseIdsFlow.value = emptySet()
            }
        }
    }

    private fun isConnected(): Boolean {
        return networkMonitor.isNetworkAvailable()
    }
}