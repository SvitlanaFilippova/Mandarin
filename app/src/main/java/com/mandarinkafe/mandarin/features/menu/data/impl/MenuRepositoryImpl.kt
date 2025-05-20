package com.mandarinkafe.mandarin.features.menu.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.network.NetworkClient
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.favorites.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.features.menu.data.dto.CategoryDto
import com.mandarinkafe.mandarin.features.menu.data.dto.MenuResponse
import com.mandarinkafe.mandarin.features.menu.data.mapper.hasParent
import com.mandarinkafe.mandarin.features.menu.data.mapper.parentName
import com.mandarinkafe.mandarin.features.menu.data.mapper.subName
import com.mandarinkafe.mandarin.features.menu.data.mapper.toDomain
import com.mandarinkafe.mandarin.features.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.applyTypography
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Singleton
class MenuRepositoryImpl @Inject constructor(
    private val networkClient: NetworkClient,
    private val favoritesRepository: FavoritesRepository
) : MenuRepository {

    private val _menu =
        MutableStateFlow<Resource<List<MealCategory>>>(Resource.Loading())
    override val menu: StateFlow<Resource<List<MealCategory>>> = _menu.asStateFlow()

    override fun getMenu(): Flow<Resource<List<MealCategory>>> {
        when (_menu.value) {
            is Resource.Error -> {
                Log.d("DEBUG  MenuRepository", "getMenu - Resource.Error")
            }

            is Resource.Loading -> {
                Log.d("DEBUG  MenuRepository", "getMenu - Resource.Loading")
                fetchMenuWithRetries()
            }

            is Resource.Success -> {
                Log.d("DEBUG  MenuRepository", "getMenu - Данные уже есть, возвращаю кэш menu")
            }
        }
        return menu
    }

    // Метод для получения актуальной информации о блюде по его id
    override fun getMealById(id: String): Meal? {
        val currentMenu = menu.value

        if (currentMenu is Resource.Success) {
            currentMenu.data?.forEach { category ->
                val meal = findMealRecursively(category, id)
                if (meal != null) return meal
            }
        }
        return null
    }

    private fun findMealRecursively(category: MealCategory, id: String): Meal? {
        // Ищем в текущей категории
        category.meals?.firstOrNull { it.id == id }?.let { return it }

        // Если не нашли — ищем во вложенных подкатегориях
        category.subCategories?.forEach { subCategory ->
            val found = findMealRecursively(subCategory, id)
            if (found != null) return found
        }
        return null
    }

    // Метод для принудительного обновления
    override suspend fun forceRefresh() {
        Log.d("DEBUG  MenuRepository", "forceRefresh")
        fetchMenuWithRetries()
    }

    private fun fetchMenuWithRetries(maxAttempts: Int = 3, delayMs: Long = 500) {
        Log.d("DEBUG  MenuRepository", "запуск fetchMenuWithRetries")
        CoroutineScope(Dispatchers.IO).launch {
            var attempts = 0
            while (attempts < maxAttempts) {
                _menu.value = Resource.Loading()
                try {
                    val response = networkClient.getMenu()
                    if (response.resultCode == -1) {
                        _menu.value =
                            Resource.Error("Проверьте подключение к интернету")
                    }
                    if (response.resultCode == HTTP_SUCCESS && (response as MenuResponse).itemCategories != null) {
                        val categories = response.itemCategories
                        val data = buildMenuStructure(categories)
                        _menu.value = Resource.Success(data)
                        return@launch
                    } else {
                        _menu.value = Resource.Error("Ошибка сервера или пустой ответ")
                    }
                } catch (e: Exception) {
                    _menu.value = Resource.Error("Что-то пошло не так. Ошибка: ${e.message}")
                }
                attempts++
                delay(delayMs)
            }
            // Если не удалось после всех попыток
            if (_menu.value !is Resource.Success) {
                _menu.value = Resource.Error("Не удалось загрузить меню после $maxAttempts попыток")
            }
        }
    }

    override fun fetchMenuIfNeeded() {
        if (_menu.value !is Resource.Success && _menu.value !is Resource.Loading) {
            fetchMenuWithRetries()
        }
    }

    private fun buildMenuStructure(menuDto: List<CategoryDto>?): List<MealCategory> {
        if (menuDto.isNullOrEmpty()) {
            Log.e("DEBUG", "menuDto оказался null или пустым")
            return emptyList()
        }
        val storedFavorites = favoritesRepository.getFavorites().map { it.id }
        val childCategoriesMap = groupSubcategories(menuDto)
        val topLevelCategories = menuDto.filter { !it.hasParent() }
        val topLevelNames = topLevelCategories.map { it.name }.toSet()

        val result = mutableListOf<MealCategory>()

        // 1. Обработка родительских категорий
        for (parent in topLevelCategories) {
            val subCategories = childCategoriesMap[parent.name]

            if (subCategories.isNullOrEmpty()) {
                // Нет подкатегорий — обычная категория с блюдами
                result.add(
                    parent.toDomain(
                        storedFavorites = storedFavorites
                    )
                )
            } else {
                // Есть подкатегории — собрать как категорию с subCategories
                result.add(buildParentCategory(parent, subCategories, storedFavorites))
            }
        }

        // 2. Обработка случайных подкатегорий без родителя
        for (category in menuDto.filter { it.hasParent() }) {
            val parentName = category.parentName()
            if (!topLevelNames.contains(parentName)) {
                result.add(buildLonelySubcategory(category, storedFavorites))
            }
        }
        return result
    }

    private fun groupSubcategories(menuDto: List<CategoryDto>): Map<String, List<CategoryDto>> {
        return menuDto
            .filter { it.hasParent() }
            .groupBy { it.parentName() }
    }

    private fun buildParentCategory(
        parentDto: CategoryDto,
        subCategories: List<CategoryDto>?,
        storedFavorites: List<String>,
    ): MealCategory {
        return MealCategory(
            id = parentDto.id,
            name = parentDto.name.applyTypography(),
            meals = null,
            subCategories = subCategories?.map { subDto ->
                subDto.copy(name = subDto.subName()).toDomain(
                    storedFavorites = storedFavorites
                )
            },
            tabIcon = parentDto.buttonImageUrl,
            description = parentDto.description.orEmpty().applyTypography(),
            isHidden = parentDto.isHidden == true
        )
    }

    private fun buildLonelySubcategory(
        category: CategoryDto,
        storedFavorites: List<String>
    ): MealCategory {
        Log.w("DEBUG", "Подкатегория '${category.name}' без родителя")
        return category.copy(name = category.subName()).toDomain(storedFavorites)
    }
}