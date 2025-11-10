package com.mandarinkafe.mandarin.features.favorites.data.impl

import com.mandarinkafe.mandarin.core.domain.api.FavoritesReader
import com.mandarinkafe.mandarin.core.domain.api.FavoritesWriter
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.FavoriteRecord
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.auth.domain.impl.AuthStateChecker
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FavoritesRepositoryImpl(
    private val storage: FavoritesStorage,
    private val validator: FavoritesValidator,
    private val remoteDataSource: FavoritesRemoteDataSource,
    private val authStateChecker: AuthStateChecker,
) : FavoritesReader, FavoritesWriter {

    private var currentRawRecords = mutableSetOf<FavoriteRecord>()

    private val _favoriteItems = MutableStateFlow<Resource<List<CustomizedMeal>>>(Idle())
    override fun observeFavorites(): Flow<Resource<List<CustomizedMeal>>> =
        _favoriteItems.asStateFlow()

    private val _baseIdsFlow = MutableStateFlow<Set<String>>(emptySet())
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    override fun observeBaseFavoritesIds(): Flow<Set<String>> = _baseIdsFlow.asStateFlow()
    private val mutex = Mutex()

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


    override suspend fun toggleFavorite(custom: CustomizedMeal) = mutex.withLock {
        // Шаг 1: Синхронизация с сервером для получения актуальной версии
        sync()

        // Шаг 2: Применяем локальное изменение
        val localResult = storage.getFavorites()
        val localFavorites = when (localResult) {
            is FavoritesStorageResult.Success -> localResult.favorites
            is FavoritesStorageResult.Corrupted -> emptySet()
        }

        // Проверяем, существует ли уже такая запись
        val existingStored = localFavorites.find { stored ->
            stored.mealId == custom.meal.id &&
                    stored.addsIds.toSet() == custom.adds.map { it.id }.toSet() &&
                    stored.modifiers.toSet() == custom.modifiers.toSet()
        }

        if (existingStored != null) {
            // Удаляем избранное
            val updatedFavorites = localFavorites.filter { it != existingStored }.toSet()
            storage.saveFavorites(updatedFavorites)
            currentRawRecords = updatedFavorites.toFavoriteRecords()
            updateFavorites(currentRawRecords)
            false
        } else {
            // Добавляем избранное
            val createdAt = getCurrentTimeMillis()
            val record = custom.toFavoriteRecord(createdAt, updatedAt = 0L)
            val stored = record.toStored()
            val updatedFavorites = localFavorites + stored
            storage.saveFavorites(updatedFavorites)
            currentRawRecords = updatedFavorites.toFavoriteRecords()
            updateFavorites(currentRawRecords)
            true
        }

        // Шаг 3: Отправляем избранное на сервер и получаем обновленную версию с updatedAt (только если авторизован)
        if (authStateChecker.isAuthorizedFast()) {
            val currentFavorites = when (val result = storage.getFavorites()) {
                is FavoritesStorageResult.Success -> result.favorites
                is FavoritesStorageResult.Corrupted -> emptySet()
            }
            val updatedFavorites = remoteDataSource.syncFavorites(currentFavorites)

            // Сохраняем обновленное избранное с updatedAt от сервера
            storage.saveFavorites(updatedFavorites.items)
            storage.updateLastUpdated(updatedFavorites.lastUpdated)

            // Обновляем внутреннее состояние
            currentRawRecords = updatedFavorites.items.toFavoriteRecords()
            updateFavorites(currentRawRecords)
        }
        // Если не авторизован, UI уже обновлен на шаге 2, ничего не делаем
    }

    override suspend fun toggleFavorite(meal: Meal) = mutex.withLock {
        // Шаг 1: Синхронизация с сервером для получения актуальной версии
        sync()

        // Шаг 2: Применяем локальное изменение
        val localResult = storage.getFavorites()
        val localFavorites = when (localResult) {
            is FavoritesStorageResult.Success -> localResult.favorites
            is FavoritesStorageResult.Corrupted -> emptySet()
        }

        // Проверяем, существует ли уже базовая запись
        val existingStored = localFavorites.find { stored ->
            stored.mealId == meal.id && stored.addsIds.isEmpty() && stored.modifiers.isEmpty()
        }

        if (existingStored != null) {
            // Удаляем избранное
            val updatedFavorites = localFavorites.filter { it != existingStored }.toSet()
            storage.saveFavorites(updatedFavorites)
            currentRawRecords = updatedFavorites.toFavoriteRecords()
            updateFavorites(currentRawRecords)
            false
        } else {
            // Добавляем избранное
            val createdAt = getCurrentTimeMillis()
            val record = meal.toFavoriteRecord(createdAt, updatedAt = 0L)
            val stored = record.toStored()
            val updatedFavorites = localFavorites + stored
            storage.saveFavorites(updatedFavorites)
            currentRawRecords = updatedFavorites.toFavoriteRecords()
            updateFavorites(currentRawRecords)
            true
        }

        // Шаг 3: Отправляем избранное на сервер и получаем обновленную версию с updatedAt (только если авторизован)
        if (authStateChecker.isAuthorizedFast()) {
            val currentFavorites = when (val result = storage.getFavorites()) {
                is FavoritesStorageResult.Success -> result.favorites
                is FavoritesStorageResult.Corrupted -> emptySet()
            }
            val updatedFavorites = remoteDataSource.syncFavorites(currentFavorites)

            // Сохраняем обновленное избранное с updatedAt от сервера
            storage.saveFavorites(updatedFavorites.items)
            storage.updateLastUpdated(updatedFavorites.lastUpdated)

            // Обновляем внутреннее состояние
            currentRawRecords = updatedFavorites.items.toFavoriteRecords()
            updateFavorites(currentRawRecords)
        }
        // Если не авторизован, UI уже обновлен на шаге 2, ничего не делаем
    }

    override suspend fun sync() {
        // Синхронизируем только если пользователь авторизован
        if (!authStateChecker.isAuthorizedFast()) {
            return
        }

        try {
            val localResult = storage.getFavorites()
            val localFavorites = when (localResult) {
                is FavoritesStorageResult.Success -> localResult.favorites
                is FavoritesStorageResult.Corrupted -> emptySet()
            }
            val localLastUpdated = storage.getLastUpdated()
            val remoteFavorites = remoteDataSource.getFavorites()

            if (shouldClearLocalFavorites(remoteFavorites, localLastUpdated)) {
                handleServerClearedFavorites(remoteFavorites)
            } else {
                performFavoritesMerge(localFavorites, localLastUpdated, remoteFavorites)
            }
        } catch (e: Exception) {
            Napier.e("Ошибка при синхронизации избранного", e)
        }
    }

    private fun shouldClearLocalFavorites(
        remoteFavorites: com.mandarinkafe.mandarin.features.favorites.data.models.Favorites,
        localLastUpdated: Long,
    ): Boolean {
        return remoteFavorites.items.isEmpty() && remoteFavorites.lastUpdated > localLastUpdated
    }

    private suspend fun handleServerClearedFavorites(
        remoteFavorites: com.mandarinkafe.mandarin.features.favorites.data.models.Favorites,
    ) {
        storage.saveFavorites(emptySet())
        storage.updateLastUpdated(remoteFavorites.lastUpdated)
        currentRawRecords = mutableSetOf()
        updateFavorites(currentRawRecords)
    }

    private suspend fun performFavoritesMerge(
        localFavorites: Set<StoredFavoriteMeal>,
        localLastUpdated: Long,
        remoteFavorites: com.mandarinkafe.mandarin.features.favorites.data.models.Favorites,
    ) {
        val isFirstSyncAfterAuth = isFirstSyncAfterAuthorization(localLastUpdated, localFavorites)
        var mergedFavorites =
            mergeFavorites(localFavorites, remoteFavorites.items, isFirstSyncAfterAuth)

        val serverIsNewerOrEqual = remoteFavorites.lastUpdated >= localLastUpdated
        mergedFavorites = removeFavoritesNotOnServer(
            mergedFavorites,
            remoteFavorites.items,
            serverIsNewerOrEqual,
            isFirstSyncAfterAuth,
        )

        val hasLocalChanges = checkForLocalChanges(
            localFavorites,
            remoteFavorites.items,
            serverIsNewerOrEqual,
            isFirstSyncAfterAuth,
        )

        saveMergedFavorites(mergedFavorites)
        updateLastUpdatedAndSyncIfNeeded(
            localLastUpdated,
            remoteFavorites.lastUpdated,
            hasLocalChanges
        )
        updateInternalState()
    }

    private fun isFirstSyncAfterAuthorization(
        localLastUpdated: Long,
        localFavorites: Set<StoredFavoriteMeal>,
    ): Boolean {
        return localLastUpdated == 0L && localFavorites.isNotEmpty() && localFavorites.any { it.updatedAt == 0L }
    }

    private fun removeFavoritesNotOnServer(
        mergedFavorites: Set<StoredFavoriteMeal>,
        remoteItems: Set<StoredFavoriteMeal>,
        serverIsNewerOrEqual: Boolean,
        isFirstSyncAfterAuth: Boolean,
    ): Set<StoredFavoriteMeal> {
        if (serverIsNewerOrEqual && !isFirstSyncAfterAuth) {
            val remoteItemKeys = remoteItems.toSet()
            return mergedFavorites.filter { localItem ->
                remoteItemKeys.any { remoteItem ->
                    localItem.mealId == remoteItem.mealId &&
                            localItem.addsIds.toSet() == remoteItem.addsIds.toSet() &&
                            localItem.modifiers.toSet() == remoteItem.modifiers.toSet()
                }
            }.toSet()
        }
        return mergedFavorites
    }

    private fun checkForLocalChanges(
        localFavorites: Set<StoredFavoriteMeal>,
        remoteItems: Set<StoredFavoriteMeal>,
        serverIsNewerOrEqual: Boolean,
        isFirstSyncAfterAuth: Boolean,
    ): Boolean {
        if (isFirstSyncAfterAuth) return true

        return localFavorites.any { localItem ->
            val remoteItem = remoteItems.find { remoteItem ->
                localItem.mealId == remoteItem.mealId &&
                        localItem.addsIds.toSet() == remoteItem.addsIds.toSet() &&
                        localItem.modifiers.toSet() == remoteItem.modifiers.toSet()
            }
            when {
                remoteItem == null -> !serverIsNewerOrEqual
                localItem.updatedAt == 0L -> true
                localItem.updatedAt > remoteItem.updatedAt -> true
                else -> false
            }
        }
    }

    private suspend fun saveMergedFavorites(mergedFavorites: Set<StoredFavoriteMeal>) {
        storage.saveFavorites(mergedFavorites)
    }

    private suspend fun updateLastUpdatedAndSyncIfNeeded(
        localLastUpdated: Long,
        remoteLastUpdated: Long,
        hasLocalChanges: Boolean,
    ) {
        val finalLastUpdated = maxOf(localLastUpdated, remoteLastUpdated)
        storage.updateLastUpdated(finalLastUpdated)

        if (hasLocalChanges) {
            val currentFavorites = when (val result = storage.getFavorites()) {
                is FavoritesStorageResult.Success -> result.favorites
                is FavoritesStorageResult.Corrupted -> emptySet()
            }
            val updatedFavorites = remoteDataSource.syncFavorites(currentFavorites)

            storage.saveFavorites(updatedFavorites.items)
            storage.updateLastUpdated(updatedFavorites.lastUpdated)
        }
    }

    private suspend fun updateInternalState() {
        val finalFavorites = when (val result = storage.getFavorites()) {
            is FavoritesStorageResult.Success -> result.favorites
            is FavoritesStorageResult.Corrupted -> emptySet()
        }
        currentRawRecords = finalFavorites.toFavoriteRecords()
        updateFavorites(currentRawRecords)
    }

    /**
     * Объединяет локальные и удалённые избранные.
     * Если есть дубликаты (одинаковые по mealId, addsIds, modifiers),
     * берёт версию с более свежим updatedAt.
     * При первой синхронизации после авторизации локальные элементы с updatedAt = 0L имеют приоритет.
     *
     * createdAt - время создания записи (не изменяется)
     * updatedAt - время последнего изменения записи (используется для разрешения конфликтов)
     */
    private fun mergeFavorites(
        local: Set<StoredFavoriteMeal>,
        remote: Set<StoredFavoriteMeal>,
        isFirstSyncAfterAuth: Boolean = false,
    ): Set<StoredFavoriteMeal> {
        // Создаём map для быстрого поиска по ключу (mealId + addsIds + modifiers)
        val mergedMap = mutableMapOf<StoredFavoriteMeal, StoredFavoriteMeal>()

        // Добавляем локальные избранные
        local.forEach { favorite ->
            mergedMap[favorite] = favorite
        }

        // Добавляем удалённые избранные, при конфликте берём версию с более свежим updatedAt
        remote.forEach { remoteFavorite ->
            val existing = mergedMap[remoteFavorite]
            if (existing == null) {
                // Такого избранного ещё нет, добавляем
                mergedMap[remoteFavorite] = remoteFavorite
            } else {
                // Есть дубликат, проверяем updatedAt
                // При первой синхронизации локальные элементы с updatedAt = 0L имеют приоритет
                if (isFirstSyncAfterAuth && existing.updatedAt == 0L) {
                    // Локальный элемент создан без авторизации - сохраняем его для отправки на сервер
                    // Не заменяем серверной версией
                } else if (remoteFavorite.updatedAt > existing.updatedAt) {
                    // Удаленная версия новее - используем её
                    mergedMap[remoteFavorite] = remoteFavorite
                }
                // Иначе локальная версия новее или равна - сохраняем локальную (уже в map)
            }
        }

        return mergedMap.values.toSet()
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






