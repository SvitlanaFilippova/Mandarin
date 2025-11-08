package com.mandarinkafe.mandarin.features.favorites.data.impl

import com.mandarinkafe.mandarin.core.domain.api.FavoritesReader
import com.mandarinkafe.mandarin.core.domain.api.FavoritesWriter
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.FavoriteRecord
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.favorites.data.datastore.FavoritesStorage
import com.mandarinkafe.mandarin.features.favorites.data.datastore.FavoritesStorageResult
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toFavoriteRecord
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toFavoriteRecords
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toStored
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorOther
import com.mandarinkafe.mandarin.util.Resource.Idle
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.getCurrentTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesRepositoryImpl(
    private val storage: FavoritesStorage,
    private val validator: FavoritesValidator,
) : FavoritesReader, FavoritesWriter {

    private var currentRawRecords = mutableSetOf<FavoriteRecord>()

    private val _favoriteItems = MutableStateFlow<Resource<List<CustomizedMeal>>>(Idle())
    override fun observeFavorites(): Flow<Resource<List<CustomizedMeal>>> =
        _favoriteItems.asStateFlow()

    private val _baseIdsFlow = MutableStateFlow<Set<String>>(emptySet())
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    override fun observeBaseFavoritesIds(): Flow<Set<String>> = _baseIdsFlow.asStateFlow()

    override fun getBaseFavoritesIds(): Set<String> {
        return _baseIdsFlow.value
    }

    init {
        getInitData()
    }


    override suspend fun forceRetry() {
        _favoriteItems.value = Loading()
        getInitData()
    }


    override suspend fun toggleFavorite(custom: CustomizedMeal) {
        val record = custom.toFavoriteRecord(getTimeStamp())
        proceedToggleFavorite(record)
    }

    override suspend fun toggleFavorite(meal: Meal) {
        val record = meal.toFavoriteRecord(getTimeStamp())
        proceedToggleFavorite(record)
    }

    override suspend fun sync() {
        //  TODO реализовать синхнронизацию
    }

    private suspend fun proceedToggleFavorite(record: FavoriteRecord) {
        if (currentRawRecords.contains(record)) {
            currentRawRecords.remove(record)
        } else {
            currentRawRecords.add(record)
        }

        // Сохраняем изменения
        updateFavorites(currentRawRecords)
    }

    private suspend fun updateFavorites(records: Set<FavoriteRecord>) {
        // Обновляем данные по базовым айди
        _baseIdsFlow.value = currentRawRecords
            .filterIsInstance<FavoriteRecord.Base>()
            .map { it.mealId }
            .toSet()

        // Валидируем и обновляем данные Флоу избранных
        _favoriteItems.value = validator(records)


        // Сохраняем новую информацию в БД
        val dtos = records.map { it.toStored() }.toSet()
        storage.saveFavorites(dtos)
    }

    private fun getTimeStamp(): Long {
        return getCurrentTimeMillis()
    }


    private fun getInitData() {
        scope.launch {
            _favoriteItems.value = Loading()
            val stored = storage.getFavorites()
            when (stored) {
                // Если вдруг избранные в БД были битые
                is FavoritesStorageResult.Corrupted -> {
                    _favoriteItems.value =
                        ErrorOther("Произошла критическая ошибка при попытке получения избранных. Пришлось их обнулить :( ")
                    _baseIdsFlow.value = emptySet()
                    currentRawRecords = mutableSetOf()
                }

                // Данные их БД получены успешно
                is FavoritesStorageResult.Success -> {
                    // Обновляем кэш "сырых" данных
                    currentRawRecords = stored.favorites.toFavoriteRecords()

                    // Обновляем данные по базовым айди
                    _baseIdsFlow.value = currentRawRecords
                        .filterIsInstance<FavoriteRecord.Base>()
                        .map { it.mealId }
                        .toSet()

                    // Валидируем и обновляем данные
                    _favoriteItems.value = validator(currentRawRecords)
                }
            }
        }
    }
}






