package com.mandarinkafe.mandarin.core.data.impl

import com.mandarinkafe.mandarin.core.data.api.MenuFetcher
import com.mandarinkafe.mandarin.core.data.network.api.ServerApi
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.features.menu.data.dto.ModifierGroupsResponse
import com.mandarinkafe.mandarin.features.menu.data.mapper.toDomain
import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.features.menu.domain.toMealAdditionalCategory
import com.mandarinkafe.mandarin.util.Constants.CATEGORY_ADDS
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.getCurrentTimeMillis
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MenuCacheImpl(
    private val fetcher: MenuFetcher,
    private val serverApi: ServerApi,
) : MenuCache {
    override var lastRefreshTime: Long = 0
        private set
    private val _allVisibleMenu =
        MutableStateFlow<Resource<List<MealCategory>>>(Resource.Idle())
    private val _mainMenu = MutableStateFlow<Resource<List<MealCategory>>>(Resource.Idle())
    private val _addonsCategories = MutableStateFlow<List<MealAdditionalCategory>>(emptyList())
    private val _deliveryItems = MutableStateFlow<MealCategory?>(null)

    // Кэш для групп модификаторов
    private var modifierGroupsCache: Map<String, ModifierGroup> = emptyMap()
    private val modifierGroupsMutex = Mutex()
    private var modifierGroupsLastFetchTime: Long = 0

    override val allVisibleMenu: StateFlow<Resource<List<MealCategory>>> =
        _allVisibleMenu.asStateFlow()
    override val mainMenu: StateFlow<Resource<List<MealCategory>>> = _mainMenu.asStateFlow()
    override val addonsCategories: StateFlow<List<MealAdditionalCategory>> =
        _addonsCategories.asStateFlow()
    override val deliveryItems: StateFlow<MealCategory?> = _deliveryItems.asStateFlow()


    override fun fetchMenuIfNeeded() {
        val isLoading = _allVisibleMenu.value is Resource.Loading
        val current = _allVisibleMenu.value
        if (current is Resource.Success || isLoading) return
        CoroutineScope(Dispatchers.Default).launch {
            _allVisibleMenu.value = Resource.Loading()
            val result = fetchWithRetries()
            _allVisibleMenu.value = result
        }
    }

    override suspend fun forceRefresh(fetcher: suspend () -> Resource<List<MealCategory>>) {
        val isLoading = _allVisibleMenu.value is Resource.Loading
        if (isLoading) return
        _allVisibleMenu.value = Resource.Loading()
        val result = fetcher()
        if (result is Resource.Success) {
            proceedSuccessData(result.data)
        } else {
            _mainMenu.value = result
        }
        _allVisibleMenu.value = result
    }

    private suspend fun fetchWithRetries(): Resource<List<MealCategory>> {
        var attempts = 0
        while (attempts < MAX_ATTEMPTS) {
            try {
                val result = fetcher.fetchMenu()
                when (result) {
                    is Resource.Success -> {
                        return Resource.Success(proceedSuccessData(result.data))
                    }

                    is Resource.ErrorNoInternet<*> -> return result
                    else -> Napier.e("fetchWithRetries. ${result.message}")
                }
            } catch (e: Exception) {
                Napier.e("fetchWithRetries. Exception: ${e.message}")
            }
            attempts++
            delay(DELAY_BEFORE_NEXT_ATTEMPT)
        }
        return Resource.ErrorOther("Не удалось загрузить меню после $attempts попыток")
    }

    // ================= Helper Methods =================
    private fun proceedSuccessData(data: List<MealCategory>?): List<MealCategory> {
        lastRefreshTime = getCurrentTimeMillis()
        val rootCategories = data ?: emptyList()

        val visible = filterVisibleMenu(rootCategories)
        _allVisibleMenu.value = Resource.Success(visible)

        val addons = extractAddons(visible)
        _addonsCategories.value = addons

        val delivery = extractSpecialCategory(
            allCategories = visible,
            criterionName = DELIVERY_CATEGORY_NAME
        )
        _deliveryItems.value = delivery

        val main = buildMainMenu(
            categories = visible,
            addons = addons,
            delivery = delivery,
            recommends = extractSpecialCategory(
                allCategories = visible,
                criterionName = RECOMMENDS_CATEGORY_NAME
            )
        )
        _mainMenu.value = Resource.Success(main)

        return visible
    }

    private fun filterVisibleMenu(categories: List<MealCategory>): List<MealCategory> =
        categories.mapNotNull { category ->
            if (category.isHidden) return@mapNotNull null
            val sub = category.subCategories?.let { filterVisibleMenu(it) }
            val meals = category.meals?.filter { !it.isHidden }?.takeIf { it.isNotEmpty() }
            if (sub == null && meals == null) return@mapNotNull null
            category.copy(subCategories = sub, meals = meals)
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

    private fun extractSpecialCategory(
        allCategories: List<MealCategory>,
        criterionName: String,
    ): MealCategory? {
        fun dfs(cat: MealCategory): MealCategory? {
            if (cat.name.equals(criterionName, ignoreCase = true)) {
                // фильтруем скрытые блюда, если есть
                val meals = cat.meals?.filter { !it.isHidden }
                return cat.copy(meals = meals)
            }
            return cat.subCategories?.firstNotNullOfOrNull { dfs(it) }
        }

        val deliveryCategory = allCategories.firstNotNullOfOrNull { dfs(it) }
        return deliveryCategory
    }

    private fun buildMainMenu(
        categories: List<MealCategory>,
        addons: List<MealAdditionalCategory>,
        delivery: MealCategory?,
        recommends: MealCategory?,
    ): List<MealCategory> =
        categories.mapNotNull { category ->
            // Проверяем, не является ли категория добавкой или доставкой
            if (addons.any { it.id == category.id }) {
                return@mapNotNull null
            }
            if (delivery?.id == category.id) {
                return@mapNotNull null
            }
            if (recommends?.id == category.id) {
                return@mapNotNull null
            }

            val sub = category.subCategories?.let {
                buildMainMenu(
                    categories = it,
                    addons = addons,
                    delivery = delivery,
                    recommends = recommends
                )
            }
            val meals = category.meals?.takeIf { it.isNotEmpty() }

            if (sub == null && meals == null) {
                return@mapNotNull null
            }
            category.copy(subCategories = sub, meals = meals)
        }

    // ====== Поиск блюд ======
    override fun getMealById(id: String): Meal? {
        val current = _allVisibleMenu.value
        if (current is Resource.Success) {
            current.data?.forEach { category ->
                val result = findMealById(category, id)
                if (result != null) {
                    return result
                }
            }
        }
        return null
    }

    override fun getMealsBySku(sku: String): List<Meal> {
        val result = mutableListOf<Meal>()
        val currentMenu = _allVisibleMenu.value
        if (currentMenu is Resource.Success) {
            currentMenu.data?.forEach { category ->
                findMealsBySku(category, sku, result)
            }
        }
        return result
    }

    // рекурсивные методы поиска (проверяют блюда внутри как самой категории, так и вложенных в неё категориях)
    private fun findMealById(category: MealCategory, id: String): Meal? {
        category.meals?.firstOrNull { it.id == id }?.let {
            return it
        }
        category.subCategories?.forEach { sub ->
            findMealById(sub, id)?.let {
                return it
            }
        }
        return null
    }

    private fun findMealsBySku(category: MealCategory, sku: String, result: MutableList<Meal>) {
        category.meals?.firstOrNull { it.sku.equals(sku, true) }?.let { result.add(it) }
        category.subCategories?.forEach { findMealsBySku(it, sku, result) }
    }

    override fun isDeliveryMeal(meal: Meal): Boolean {
        val deliveryCategory = deliveryItems.value ?: return false
        fun dfs(cat: MealCategory): Boolean {
            if (cat.meals?.any { it.id == meal.id } == true) return true
            return cat.subCategories?.any { dfs(it) } == true
        }
        return dfs(deliveryCategory)
    }

    override fun findModifierGroup(
        groupId: String,
        itemIds: List<String>,
    ): ModifierGroup {
        // Используем runBlocking для синхронного доступа к кэшу и загрузки
        return runBlocking {
            modifierGroupsMutex.withLock {
                val cached = modifierGroupsCache[groupId]
                if (cached != null) {
                    // Фильтруем items по itemIds, если они указаны
                    if (itemIds.isNotEmpty()) {
                        val filteredItems = cached.items.filter { it.id in itemIds }
                        cached.copy(items = filteredItems)
                    } else {
                        cached
                    }
                } else {
                    // Если кэш пуст, загружаем
                    loadModifierGroupsUnsafe()
                    val group = modifierGroupsCache[groupId]
                    if (group != null) {
                        if (itemIds.isNotEmpty()) {
                            val filteredItems = group.items.filter { it.id in itemIds }
                            group.copy(items = filteredItems)
                        } else {
                            group
                        }
                    } else {
                        // Если группа не найдена, возвращаем пустую группу
                        ModifierGroup(
                            id = groupId,
                            name = "",
                            items = emptyList(),
                            isSingleChoice = false,
                            isRequired = false,
                            maxQuantity = 0
                        )
                    }
                }
            }
        }
    }

    private suspend fun loadModifierGroups() {
        modifierGroupsMutex.withLock {
            loadModifierGroupsUnsafe()
        }
    }

    private suspend fun loadModifierGroupsUnsafe() {
        // Проверяем, не загружали ли мы недавно (кэш на 1 час)
        val now = getCurrentTimeMillis()
        val cacheAge = now - modifierGroupsLastFetchTime
        if (modifierGroupsCache.isNotEmpty() && cacheAge < MODIFIER_GROUPS_CACHE_TTL_MS) {
            return
        }

        try {
            val response = serverApi.getModifierGroups()
            if (response.resultCode == HTTP_SUCCESS && response is ModifierGroupsResponse) {
                val groups = response.data?.map { it.toDomain() } ?: emptyList()
                modifierGroupsCache = groups.associateBy { it.id }
                modifierGroupsLastFetchTime = now
            } else {
                Napier.e("MenuCacheImpl: Ошибка загрузки групп модификаторов, resultCode=${response.resultCode}")
            }
        } catch (e: Exception) {
            Napier.e("MenuCacheImpl: Исключение при загрузке групп модификаторов: ${e.message}", e)
        }
    }

    private companion object {
        const val MAX_ATTEMPTS = 3
        const val DELAY_BEFORE_NEXT_ATTEMPT: Long = 400L

        const val RECOMMENDS_CATEGORY_NAME = "Рекомендованные"
        const val DELIVERY_CATEGORY_NAME = "Доставка"

        const val MODIFIER_GROUPS_CACHE_TTL_MS = 3_600_000L // 1 час
    }
}
