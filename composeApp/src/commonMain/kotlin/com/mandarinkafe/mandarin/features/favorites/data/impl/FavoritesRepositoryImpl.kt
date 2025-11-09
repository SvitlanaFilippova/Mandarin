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
import com.mandarinkafe.mandarin.features.favorites.data.models.StoredFavoriteMeal
import com.mandarinkafe.mandarin.features.favorites.data.remote.FavoritesRemoteDataSource
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorOther
import com.mandarinkafe.mandarin.util.Resource.Idle
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.getCurrentTimeMillis
import io.github.aakira.napier.Napier
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
    private val remoteDataSource: FavoritesRemoteDataSource,
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
        try {
            // Получаем локальные избранные
            val localResult = storage.getFavorites()
            val localFavorites = when (localResult) {
                is FavoritesStorageResult.Success -> localResult.favorites
                is FavoritesStorageResult.Corrupted -> emptySet()
            }

            // Получаем удалённые избранные с сервера
            val remoteFavorites = remoteDataSource.getFavorites()

            // Объединяем локальные и удалённые избранные
            // Если есть дубликаты (одинаковые по equals, но разные timestamp),
            // берём версию с более свежим timestamp
            val mergedFavorites = mergeFavorites(localFavorites, remoteFavorites)

            // Сохраняем объединённый результат локально
            storage.saveFavorites(mergedFavorites)

            // Обновляем внутреннее состояние
            currentRawRecords = mergedFavorites.toFavoriteRecords()
            updateFavorites(currentRawRecords)

            // Отправляем объединённый результат на сервер
            remoteDataSource.syncFavorites(currentRawRecords)
        } catch (e: Exception) {
            // В случае ошибки просто игнорируем синхронизацию
            // Локальные данные остаются без изменений
        }
    }

    /**
     * Объединяет локальные и удалённые избранные.
     * Если есть дубликаты (одинаковые по mealId, addsIds, modifiers),
     * берёт версию с более свежим timestamp.
     */
    private fun mergeFavorites(
        local: Set<StoredFavoriteMeal>,
        remote: Set<StoredFavoriteMeal>
    ): Set<StoredFavoriteMeal> {
        Napier.d("[FavoritesSync] mergeFavorites: начало объединения")
        // Создаём map для быстрого поиска по ключу (mealId + addsIds + modifiers)
        val mergedMap = mutableMapOf<StoredFavoriteMeal, StoredFavoriteMeal>()

        // Добавляем локальные избранные
        var localAdded = 0
        local.forEach { favorite ->
            mergedMap[favorite] = favorite
            localAdded++
        }
        Napier.d("[FavoritesSync] mergeFavorites: добавлено локальных избранных: $localAdded")

        // Добавляем удалённые избранные, при конфликте берём версию с более свежим timestamp
        var remoteAdded = 0
        var conflictsResolved = 0
        remote.forEach { remoteFavorite ->
            val existing = mergedMap[remoteFavorite]
            if (existing == null) {
                // Такого избранного ещё нет, добавляем
                mergedMap[remoteFavorite] = remoteFavorite
                remoteAdded++
            } else {
                // Есть дубликат, берём версию с более свежим timestamp
                conflictsResolved++
                if (remoteFavorite.timestamp > existing.timestamp) {
                    Napier.d("[FavoritesSync] mergeFavorites: конфликт разрешён в пользу удалённой версии (timestamp: ${remoteFavorite.timestamp} > ${existing.timestamp})")
                    mergedMap[remoteFavorite] = remoteFavorite
                } else {
                    Napier.d("[FavoritesSync] mergeFavorites: конфликт разрешён в пользу локальной версии (timestamp: ${existing.timestamp} >= ${remoteFavorite.timestamp})")
                }
            }
        }
        Napier.d("[FavoritesSync] mergeFavorites: добавлено удалённых избранных: $remoteAdded, разрешено конфликтов: $conflictsResolved")

        val result = mergedMap.values.toSet()
        Napier.d("[FavoritesSync] mergeFavorites: итоговое количество записей: ${result.size}")
        return result
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






