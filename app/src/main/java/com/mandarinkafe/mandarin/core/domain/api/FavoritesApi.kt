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

    /** Получить уже **валидный**, очищенный от пропавших или устаревших, список. */
    suspend fun getFavorites(): Resource<List<CustomizedMeal>>

    /** Проверяет наличие «чистого» блюда в списке избранных.*/
    suspend fun checkIfFavorite(custom: CustomizedMeal): Boolean

    /** Проверяет наличие кастомизированное блюда в списке избранных.*/
    suspend fun checkIfFavorite(meal: Meal): Boolean

    /** Эмитит новый список при каждом изменении избранных.*/

    fun observeFavoritesItems(): Flow<Resource<List<CustomizedMeal>>>

    /** Эмитит новый список ID при каждом изменении избранных "базовых" блюд.*/
    fun observeFavoritesBaseMealIDs(): Flow<Set<String>>
}