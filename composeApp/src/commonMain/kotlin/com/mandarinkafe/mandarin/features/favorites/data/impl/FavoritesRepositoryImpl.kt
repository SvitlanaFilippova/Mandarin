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

        val isNowFavorite = if (existingStored != null) {
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

        val isNowFavorite = if (existingStored != null) {
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
            // Получаем локальные избранные и lastUpdated
            val localResult = storage.getFavorites()
            val localFavorites = when (localResult) {
                is FavoritesStorageResult.Success -> localResult.favorites
                is FavoritesStorageResult.Corrupted -> emptySet()
            }
            val localLastUpdated = storage.getLastUpdated()

            // Получаем удалённые избранные с сервера
            val remoteFavorites = remoteDataSource.getFavorites()

            // Проверяем: если сервер вернул пустое избранное и его lastUpdated новее локального,
            // это означает, что избранное было очищено на сервере - нужно очистить локальное избранное
            val shouldClearLocal =
                remoteFavorites.items.isEmpty() && remoteFavorites.lastUpdated > localLastUpdated
            if (shouldClearLocal) {
                storage.saveFavorites(emptySet())
                storage.updateLastUpdated(remoteFavorites.lastUpdated)
                currentRawRecords = mutableSetOf()
                updateFavorites(currentRawRecords)
            } else {
                // Объединяем локальные и удалённые избранные по updatedAt каждого элемента
                var mergedFavorites = mergeFavorites(localFavorites, remoteFavorites.items)

                // Удаляем записи, которые есть локально, но отсутствуют на сервере,
                // если серверная версия избранного новее или равна локальной
                val serverIsNewerOrEqual = remoteFavorites.lastUpdated >= localLastUpdated
                if (serverIsNewerOrEqual) {
                    val remoteItemKeys = remoteFavorites.items.toSet()
                    mergedFavorites = mergedFavorites.filter { localItem ->
                        remoteItemKeys.any { remoteItem ->
                            localItem.mealId == remoteItem.mealId &&
                                    localItem.addsIds.toSet() == remoteItem.addsIds.toSet() &&
                                    localItem.modifiers.toSet() == remoteItem.modifiers.toSet()
                        }
                    }.toSet()
                }

                // Проверяем, были ли локальные изменения ДО мержа
                // Изменения есть, если:
                // 1. Есть локальные записи с updatedAt = 0 (измененные локально)
                // 2. Есть локальные записи, которых нет на сервере (но только если локальная версия новее)
                // 3. Есть локальные записи с updatedAt > серверного updatedAt
                val hasLocalChanges = localFavorites.any { localItem ->
                    val remoteItem = remoteFavorites.items.find { remoteItem ->
                        localItem.mealId == remoteItem.mealId &&
                                localItem.addsIds.toSet() == remoteItem.addsIds.toSet() &&
                                localItem.modifiers.toSet() == remoteItem.modifiers.toSet()
                    }
                    when {
                        remoteItem == null -> !serverIsNewerOrEqual // запись только локально, но только если локальная версия новее
                        localItem.updatedAt == 0L -> true // запись изменена локально
                        localItem.updatedAt > remoteItem.updatedAt -> true // локальная версия новее
                        else -> false
                    }
                }

                // Сохраняем объединённый результат локально
                storage.saveFavorites(mergedFavorites)

                // Обновляем lastUpdated (берем максимальный из локального и удаленного)
                val finalLastUpdated = maxOf(localLastUpdated, remoteFavorites.lastUpdated)
                storage.updateLastUpdated(finalLastUpdated)

                // Если после мержа есть локальные изменения, отправляем избранное на сервер
                if (hasLocalChanges) {
                    val currentFavorites = when (val result = storage.getFavorites()) {
                        is FavoritesStorageResult.Success -> result.favorites
                        is FavoritesStorageResult.Corrupted -> emptySet()
                    }
                    val updatedFavorites = remoteDataSource.syncFavorites(currentFavorites)

                    // Сохраняем обновленное избранное с updatedAt от сервера
                    storage.saveFavorites(updatedFavorites.items)
                    storage.updateLastUpdated(updatedFavorites.lastUpdated)
                }

                // Обновляем внутреннее состояние
                val finalFavorites = when (val result = storage.getFavorites()) {
                    is FavoritesStorageResult.Success -> result.favorites
                    is FavoritesStorageResult.Corrupted -> emptySet()
                }
                currentRawRecords = finalFavorites.toFavoriteRecords()
                updateFavorites(currentRawRecords)
            }
        } catch (e: Exception) {
            // В случае ошибки просто игнорируем синхронизацию
            // Локальные данные остаются без изменений
            Napier.e("Ошибка при синхронизации избранного", e)
        }
    }

    override suspend fun clear() = mutex.withLock {
//        storage.clear()
//
//        // Обновляем UI
//        _favoriteItems.value = Resource.Success(emptyList())
//        _baseIdsFlow.value = emptySet()
//
//        // Получаем обновленную корзину (должна быть пустой) и обновляем lastUpdated

    }

    /**
     * Объединяет локальные и удалённые избранные.
     * Если есть дубликаты (одинаковые по mealId, addsIds, modifiers),
     * берёт версию с более свежим updatedAt.
     *
     * createdAt - время создания записи (не изменяется)
     * updatedAt - время последнего изменения записи (используется для разрешения конфликтов)
     */
    private fun mergeFavorites(
        local: Set<StoredFavoriteMeal>,
        remote: Set<StoredFavoriteMeal>,
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
                // Сравниваем по updatedAt (время последнего изменения)
                if (remoteFavorite.updatedAt > existing.updatedAt) {
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






