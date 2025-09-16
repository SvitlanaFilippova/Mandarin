package com.mandarinkafe.mandarin.core.domain.api

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.StateFlow

/**
 * Класс для хранения полученного меню и операций с ним
 */
interface MenuCache {
    /**
     * Все категории со статусом "visible", включая добавки
     */
    val allVisibleMenu: StateFlow<Resource<List<MealCategory>>>

    /**
     * Только категории (видимые), которые должны отображаться в основном меню
     */
    val mainMenu: StateFlow<Resource<List<MealCategory>>>

    /**
     * Категории добавок (видимые)
     */
    val addonsCategories: StateFlow<List<MealAdditionalCategory>>

    /**
     * Категория позиций доставки
     */
    val deliveryItems: StateFlow<MealCategory?>

    fun getMealById(id: String): Meal?
    fun getMealsBySku(sku: String): List<Meal>
    fun fetchMenuIfNeeded()
    fun isDeliveryMeal(meal: Meal): Boolean
    suspend fun forceRefresh(fetcher: suspend () -> Resource<List<MealCategory>>)
}