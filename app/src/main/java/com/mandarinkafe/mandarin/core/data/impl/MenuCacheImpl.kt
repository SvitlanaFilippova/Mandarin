package com.mandarinkafe.mandarin.core.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.api.MenuFetcher
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.menu.domain.mappers.toMealAdditionalCategory
import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.util.Constants.CATEGORY_ADDS
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_CATEGORY_NAME
import com.mandarinkafe.mandarin.util.Resource
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Singleton
class MenuCacheImpl @Inject constructor(
    private val fetcher: MenuFetcher,

    ) : MenuCache {
    private val _fullMenu = MutableStateFlow<Resource<List<MealCategory>>>(Resource.Idle())
    override val fullMenu: StateFlow<Resource<List<MealCategory>>> = _fullMenu.asStateFlow()

    private val _visibleMenu = MutableStateFlow<Resource<List<MealCategory>>>(Resource.Idle())
    override val visibleMenu: StateFlow<Resource<List<MealCategory>>> = _visibleMenu.asStateFlow()

    private val _addonsCategories = MutableStateFlow<List<MealAdditionalCategory>>(emptyList())
    override val addonsCategories: StateFlow<List<MealAdditionalCategory>> =
        _addonsCategories.asStateFlow()

    private val _deliveryItems = MutableStateFlow<MealCategory?>(null)
    override val deliveryItems: StateFlow<MealCategory?> = _deliveryItems.asStateFlow()

    override fun fetchMenuIfNeeded() {
        val current = _visibleMenu.value
        if (current is Resource.Success) {
            return
        }

        if (current is Resource.Loading) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            _visibleMenu.value = Resource.Loading()
            val result = fetchWithRetries()
            _visibleMenu.value = result
        }
    }

    private suspend fun fetchWithRetries(): Resource<List<MealCategory>> {
        var attempts = 0
        while (attempts < MAX_ATTEMPTS) {
            try {
                val result = fetcher.fetchMenu()
                when (result) {
                    is Resource.Success -> {
                        val rootCategories = result.data ?: emptyList()
                        _fullMenu.value = Resource.Success(rootCategories)
                        _addonsCategories.value = extractAddons(rootCategories)
                        _deliveryItems.value = extractDelivery(rootCategories)
                        val filteredMenu = filterVisibleCategories(rootCategories)
                        return Resource.Success(filteredMenu)
                    }

                    is Resource.ErrorNoInternet<*> -> return result
                    else -> {
                        Log.d(
                            "Menu DEBUG",
                            "fetchWithRetries. Не удалось загрузить меню. ${result.message}"
                        )
                    }
                }

            } catch (e: Exception) {
                Log.d("Menu DEBUG", "fetchWithRetries. Ошибка: ${e.message}")
            }
            attempts++
            delay(DELAY_BEFORE_NEXT_ATTEMPT)
        }
        return Resource.ErrorOther("Не удалось загрузить меню после $attempts попыток")
    }

    override suspend fun forceRefresh(fetcher: suspend () -> Resource<List<MealCategory>>) {
        _visibleMenu.value = fetcher()
    }

    // Методы для получения блюд по id / sku
    override fun getMealById(id: String): Meal? {
        val current = _fullMenu.value
        if (current is Resource.Success) {
            current.data?.forEach { category ->
                val result = findMealById(category, id)
                if (result != null) return result
            }
        }
        return null
    }

    override fun getMealsBySku(sku: String): List<Meal> {
        val result = mutableListOf<Meal>()
        val currentMenu = _fullMenu.value
        if (currentMenu is Resource.Success) {
            currentMenu.data?.forEach { category ->
                findMealsBySku(category, sku, result)
            }
        }
        return result
    }

    // рекурсивные методы поиска (проверяют блюда внутри как самой категории, так и вложенных в неё категориях)
    private fun findMealById(category: MealCategory, id: String): Meal? {
        category.meals?.firstOrNull { it.id == id }?.let { return it }
        category.subCategories?.forEach { sub ->
            findMealById(sub, id)?.let { return it }
        }
        return null
    }

    private fun findMealsBySku(category: MealCategory, sku: String, result: MutableList<Meal>) {
        category.meals?.firstOrNull { it.sku.equals(sku, true) }?.let { result.add(it) }
        category.subCategories?.forEach { findMealsBySku(it, sku, result) }
    }

    private fun extractAddons(categories: List<MealCategory>): List<MealAdditionalCategory> {
        val result = mutableListOf<MealCategory>()

        fun dfs(cat: MealCategory, path: List<String>) {
            val curPath = path + cat.name

            if (CATEGORY_ADDS in curPath) {
                result += cat
            }
            cat.subCategories?.forEach { dfs(it, curPath) }
        }

        categories.forEach { dfs(it, emptyList()) }
        return result.map { it.toMealAdditionalCategory() }
    }

    private fun extractDelivery(categories: List<MealCategory>): MealCategory? {
        fun dfs(cat: MealCategory, depth: Int): MealCategory? {
            if (cat.name.equals(DELIVERY_CATEGORY_NAME, ignoreCase = true)) {
                return cat
            }
            return cat.subCategories?.firstNotNullOfOrNull { dfs(it, depth + 1) }
        }

        return categories.firstNotNullOfOrNull { dfs(it, 0) }
    }

    private fun filterVisibleCategories(categories: List<MealCategory>): List<MealCategory> {
        return categories
            .filter { !it.isHidden }
            .map { category ->
                category.copy(
                    subCategories = category.subCategories?.let { filterVisibleCategories(it) }
                )
            }
    }

    private companion object {
        const val MAX_ATTEMPTS = 5
        const val DELAY_BEFORE_NEXT_ATTEMPT: Long = 100L
    }
}