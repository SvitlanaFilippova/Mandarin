package com.mandarinkafe.mandarin.menu.data.impl

import android.content.Context
import android.util.Log
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.data.network.NetworkClient
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.favorites.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.menu.data.dto.CategoryDto
import com.mandarinkafe.mandarin.menu.data.dto.MenuResponse
import com.mandarinkafe.mandarin.menu.data.mapper.hasParent
import com.mandarinkafe.mandarin.menu.data.mapper.parentName
import com.mandarinkafe.mandarin.menu.data.mapper.subName
import com.mandarinkafe.mandarin.menu.data.mapper.toDomain
import com.mandarinkafe.mandarin.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.applyTypography
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Singleton
class MenuRepositoryImpl @Inject constructor(
    private val networkClient: NetworkClient,
    private val favoritesRepository: FavoritesRepository,
    @ApplicationContext private val context: Context
) : MenuRepository {

    private val _menu =
        MutableStateFlow<Resource<List<MealCategory>>>(Resource.Loading()) // Состояние загрузки
    override val menu: StateFlow<Resource<List<MealCategory>>> = _menu.asStateFlow()

    override fun getMenu(): Flow<Resource<List<MealCategory>>> {
        // если данные уже есть, возвращаем их
        if (_menu.value is Resource.Success) return menu

        // если данных нет, начинаем загрузку
        fetchMenuFromNetwork()
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
        fetchMenuFromNetwork(true)
    }

    private fun fetchMenuFromNetwork(force: Boolean = false) {
        // Если данных нет или нужно принудительное обновление
        if (_menu.value !is Resource.Success || force) {
            CoroutineScope(Dispatchers.IO).launch {
                _menu.value = Resource.Loading()
                try {
                    // Загружаем меню
                    val response = networkClient.getMenu()
                    when (response.resultCode) {
                        -1 -> _menu.value =
                            Resource.Error(context.getString(R.string.error_no_internet))

                        HTTP_SUCCESS -> {
                            val categories = (response as MenuResponse).itemCategories
                            if (categories != null) {
                                _menu.value =
                                    Resource.Success(menuDtoToDomain(categories))
                            } else {
                                _menu.value =
                                    Resource.Error(context.getString(R.string.error_empty_menu))
                            }
                        }

                        else -> _menu.value =
                            Resource.Error(context.getString(R.string.error_server_error))
                    }
                } catch (e: Exception) {
                    _menu.value =
                        Resource.Error(context.getString(R.string.error_something_wrong, e.message))
                }
            }
        }
    }

    private fun menuDtoToDomain(menuDto: List<CategoryDto>?): List<MealCategory> {
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