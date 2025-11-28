package com.mandarinkafe.mandarin.core.domain.api

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal

interface FavoritesWriter {
    /** Добавляет или убирает запись; возвращает новое состояние (true = теперь в избранном). */
    suspend fun toggleFavorite(custom: CustomizedMeal)
    suspend fun toggleFavorite(meal: Meal)
    suspend fun sync()
}