package com.mandarinkafe.mandarin.core.domain.api

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import kotlinx.coroutines.flow.Flow

interface FavoritesApi {

    /** Добавить или убрать из избранного «чистое» блюдо.
     *  Возвращает актуальный статус isFavorite для блюда после выполнения операции*/
    suspend fun toggleFavorite(meal: Meal): Boolean

    /** Добавить или убрать из избранного кастомизированное блюдо.
     *  Возвращает актуальный статус isFavorite для блюда после выполнения операции*/
    suspend fun toggleFavorite(custom: CustomizedMeal): Boolean

    /** Получить уже **валидный**, очищенный от пропавших или устаревших, список. */
    suspend fun getFavorites(): List<CustomizedMeal>

    /** Проверяет наличие «чистого» блюда в списке избранных.*/
    suspend fun checkIfFavorite(custom: CustomizedMeal): Boolean

    /** Проверяет наличие кастомизированное блюда в списке избранных.*/
    suspend fun checkIfFavorite(meal: Meal): Boolean

    /** Эмитит новый список при каждом изменении избранных.*/
    fun observeFavorites(): Flow<List<CustomizedMeal>>
}