package com.mandarinkafe.mandarin.core.domain.api

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface FavoritesApi {

    /** Добавить или убрать из избранного «чистое» блюдо.
     *  Возвращает актуальный статус isFavorite для блюда после выполнения операции*/
    suspend fun toggleFavorite(meal: Meal)

    /** Добавить или убрать из избранного кастомизированное блюдо.
     *  Возвращает актуальный статус isFavorite для блюда после выполнения операции*/
    suspend fun toggleFavorite(custom: CustomizedMeal)

    /** Эмитит новый список при каждом изменении избранных.*/
    fun observeFavoritesItems(): Flow<Resource<List<CustomizedMeal>>>

    /** Эмитит новый список ID при каждом изменении избранных "базовых" блюд.*/
    fun observeFavoritesBaseMealIDs(): Flow<Set<String>>

    /** Принудительное обновление данных о сохранённых избранных и повторная их валидация по меню.*/
    suspend fun forceRefresh()
    suspend fun syncWithRemote()
}