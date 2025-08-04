package com.mandarinkafe.mandarin.core.domain.api

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.StateFlow

/**
 * Класс для хранения поулченного меню и операций с ним
 */
interface MenuCache {
    val fullMenu: StateFlow<Resource<List<MealCategory>>>
    val visibleMenu: StateFlow<Resource<List<MealCategory>>>
    val addonsCategories: StateFlow<List<MealAdditionalCategory>>
    val deliveryItems: StateFlow<MealCategory?>
    fun getMealById(id: String): Meal?
    fun getMealsBySku(sku: String): List<Meal>
    fun fetchMenuIfNeeded()
    suspend fun forceRefresh(fetcher: suspend () -> Resource<List<MealCategory>>)
}