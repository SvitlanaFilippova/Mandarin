package com.mandarinkafe.mandarin.core.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.api.MenuFetcher
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.util.Constants.DELAY_BEFORE_NEXT_ATTEMPT
import com.mandarinkafe.mandarin.util.Constants.MAX_ATTEMPTS
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
    private val fetcher: MenuFetcher
) : MenuCache {

    private val _menu = MutableStateFlow<Resource<List<MealCategory>>>(Resource.Idle())
    override val menu: StateFlow<Resource<List<MealCategory>>> = _menu.asStateFlow()

    override fun fetchMenuIfNeeded() {
        val current = _menu.value
        if (current is Resource.Success) {
            return
        }

        if (current is Resource.Loading) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            _menu.value = Resource.Loading()
            val result = fetchWithRetries()
            _menu.value = result
        }
    }

    private suspend fun fetchWithRetries(): Resource<List<MealCategory>> {
        var attempts = 0
        while (attempts < MAX_ATTEMPTS) {
            try {
                val result = fetcher.fetchMenu()
                when (result) {
                    is Resource.Success -> return result
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
        _menu.value = fetcher()
    }

    // Методы для получения блюд по id / sku
    override fun getMealById(id: String): Meal? {
        val current = _menu.value
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
        val current = _menu.value
        if (current is Resource.Success) {
            current.data?.forEach { category ->
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
}