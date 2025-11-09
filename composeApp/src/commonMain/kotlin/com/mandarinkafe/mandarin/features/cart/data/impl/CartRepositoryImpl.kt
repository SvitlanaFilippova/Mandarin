package com.mandarinkafe.mandarin.features.cart.data.impl

import com.mandarinkafe.mandarin.core.domain.api.CartReader
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.cart.data.Mapper.toCustomizedMeal
import com.mandarinkafe.mandarin.features.cart.data.Mapper.toStoredCartItem
import com.mandarinkafe.mandarin.features.cart.data.local.CartStorage
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem
import com.mandarinkafe.mandarin.features.cart.data.remote.CartRemoteDataSource
import com.mandarinkafe.mandarin.features.cart.data.validateBy
import com.mandarinkafe.mandarin.features.cart.domain.api.CartWriter
import com.mandarinkafe.mandarin.features.menu.domain.toMealAdditional
import com.mandarinkafe.mandarin.util.Constants.MENU_WAIT_TIMEOUT
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.getCurrentTimeMillis
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

class CartRepositoryImpl(
    private val storage: CartStorage,
    private val menuCache: MenuCache,
    private val remoteDataSource: CartRemoteDataSource,
) : CartWriter, CartReader {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var cartItems: List<CartItem> = emptyList()
    private val _cartItems = MutableStateFlow<Resource<List<CartItem>>>(Resource.Idle())
    override fun observeCartItems(): Flow<Resource<List<CartItem>>> = _cartItems.asStateFlow()

    private val _cartCount = MutableStateFlow(0)
    override fun observeCartItemsCount(): Flow<Int> = _cartCount.asStateFlow()
    private val mutex = Mutex()

    init {
        getInitData()
    }

    private fun getInitData() {
        scope.launch {
            // 1. Получаем корзину из storage
            val storedCartItems = try {
                storage.getCartItems()
            } catch (e: Exception) {
                Napier.e("[CartSync] Ошибка при чтении корзины из storage", e)
                storage.clearCart()
                _cartItems.value =
                    Resource.ErrorOther("Ошибка при чтении корзины из локального хранилища. Корзина будет очищена.")
                emptyList()
            }

            // Ждём до MENU_WAIT_TIMEOUT пока меню станет не Loading/Idle
            val menuResource = withTimeoutOrNull(MENU_WAIT_TIMEOUT) {
                menuCache.allVisibleMenu
                    .firstOrNull { it !is Resource.Loading && it !is Resource.Idle }
            } ?: menuCache.allVisibleMenu.value // если таймаут, берём последнее известное состояние

            _cartItems.value = when (menuResource) {
                is Resource.Success -> {
                    val fullMenu = menuResource.data.orEmpty()
                    val validItems = mapAndValidate(storedCartItems, fullMenu)
                    cartItems = validItems
                    _cartCount.value = validItems.sumOf { it.quantity }

                    Resource.Success(validItems)
                }

                is Resource.ErrorEmptyData -> Resource.ErrorEmptyData()
                is Resource.ErrorNoInternet -> Resource.ErrorNoInternet()
                is Resource.ErrorOther -> Resource.ErrorOther(menuResource.message ?: "Ошибка меню")
                is Resource.Loading -> Resource.Loading()
                is Resource.Idle -> Resource.Idle()
            }
        }
    }

    override suspend fun forceRetry() {
        _cartItems.value = Resource.Loading()
        getInitData()
    }

    private fun mapAndValidate(
        raw: List<StoredCartItem>,
        menu: List<MealCategory>,
    ): List<CartItem> {
        val valid = mutableListOf<CartItem>()

        val allMeals = flattenMeals(menu).associateBy { it.id }

        for (item in raw) {
            val baseMeal = allMeals[item.mealId]
            if (baseMeal == null) {
                continue
            }

            try {
                val adds = item.addsIds.mapNotNull { allMeals[it]?.toMealAdditional() }
                val mods = item.modifiers.validateBy(baseMeal.modifiers)
                val customizedMeal = item.toCustomizedMeal(baseMeal, adds, mods)

                valid += CartItem(
                    id = item.id,
                    customizedMeal = customizedMeal,
                    quantity = item.quantity,
                    comment = item.comment
                )
            } catch (e: Exception) {
                        Napier.e("[CartSync] Mapping failed for item: $item", e)
            }
        }
        return valid
    }

    private fun flattenMeals(categories: List<MealCategory>): List<Meal> {
        val result = mutableListOf<Meal>()
        fun dfs(cat: MealCategory) {
            result += cat.meals.orEmpty()
            cat.subCategories?.forEach { dfs(it) }
        }
        categories.forEach { dfs(it) }
        return result
    }

    override suspend fun addOrUpdateItem(item: CartItem): Boolean = mutex.withLock {
        Napier.d("[CartSync] addOrUpdateItem: начало, id=${item.id}, mealId=${item.customizedMeal.meal.id}, quantity=${item.quantity}")
        
        // Шаг 1: Синхронизация с сервером для получения актуальной версии
        Napier.d("[CartSync] addOrUpdateItem: синхронизация с сервером перед изменением")
        sync()
        
        // Шаг 2: Применяем локальное изменение
        val existingStored = storage.getCartItems().find { it.id == item.id }
        val createdAt = if (existingStored == null) {
            // Новый элемент - устанавливаем createdAt
            getCurrentTimeMillis()
            } else {
            // Существующий элемент - сохраняем старый createdAt
            existingStored.createdAt
        }
        // updatedAt устанавливается в 0L - маркер "эта позиция изменена/новая, обновляй"
        val storedItem = item.toStoredCartItem(createdAt, updatedAt = 0L)
        Napier.d("[CartSync] addOrUpdateItem: сохранение в storage, id=${item.id}, createdAt=$createdAt, updatedAt=0L (маркер изменения)")
        storage.addOrUpdateItem(storedItem)
        
        // Обновляем UI из storage
        updateUIFromStorage()
        
        var wasUpdated = cartItems.any { it.id == item.id && it != item }
        if (!wasUpdated && existingStored == null) {
            wasUpdated = true // Новый элемент добавлен
        }
        
        // Шаг 3: Отправляем корзину на сервер и получаем обновленную версию с updatedAt
        Napier.d("[CartSync] addOrUpdateItem: отправка корзины на сервер после изменения")
        val localCart = storage.getCartItems()
        val updatedCart = remoteDataSource.syncCart(localCart)
        
        // Сохраняем обновленную корзину с updatedAt от сервера
        Napier.d("[CartSync] addOrUpdateItem: сохранение обновленной корзины с сервера, элементов=${updatedCart.items.size}, lastUpdated=${updatedCart.lastUpdated}")
        updatedCart.items.forEach { updatedItem ->
            storage.addOrUpdateItem(updatedItem)
        }
        storage.updateLastUpdated(updatedCart.lastUpdated)
        
        // Обновляем UI
        updateUIFromStorage()
        
        return wasUpdated
    }

    override suspend fun deleteItemById(id: String) = mutex.withLock {
        Napier.d("[CartSync] deleteItemById: начало, id=$id")
        
        // Шаг 1: Синхронизация с сервером для получения актуальной версии
        Napier.d("[CartSync] deleteItemById: синхронизация с сервером перед удалением")
        sync()
        
        // Шаг 2: Применяем локальное изменение - физически удаляем элемент
            storage.deleteItemById(id)
        Napier.d("[CartSync] deleteItemById: элемент удален из storage, id=$id")
        
        // Обновляем UI из storage
        updateUIFromStorage()
        
        // Шаг 3: Отправляем корзину на сервер и получаем обновленную версию с updatedAt
        Napier.d("[CartSync] deleteItemById: отправка корзины на сервер после удаления")
        val localCart = storage.getCartItems()
        val updatedCart = remoteDataSource.syncCart(localCart)
        
        // Сохраняем обновленную корзину с updatedAt от сервера
        Napier.d("[CartSync] deleteItemById: сохранение обновленной корзины с сервера, элементов=${updatedCart.items.size}, lastUpdated=${updatedCart.lastUpdated}")
        updatedCart.items.forEach { updatedItem ->
            storage.addOrUpdateItem(updatedItem)
        }
        storage.updateLastUpdated(updatedCart.lastUpdated)
        
        // Обновляем UI
        updateUIFromStorage()
    }

    override suspend fun clear() = mutex.withLock {
        Napier.d("[CartSync] clear: начало очистки корзины")
        
        // Шаг 1: Синхронизация с сервером для получения актуальной версии
        Napier.d("[CartSync] clear: синхронизация с сервером перед очисткой")
        sync()
        
        // Шаг 2: Применяем локальное изменение - очищаем корзину
        storage.clearCart()
        Napier.d("[CartSync] clear: корзина очищена локально")
        
        // Обновляем UI
        cartItems = emptyList()
        _cartItems.value = Resource.Success(emptyList())
        _cartCount.value = 0

        // Шаг 3: Отправляем DELETE /cart на сервер
        Napier.d("[CartSync] clear: отправка DELETE /cart на сервер")
        remoteDataSource.clearCart()
        
        // Получаем обновленную корзину (должна быть пустой) и обновляем lastUpdated
        val updatedCart = remoteDataSource.getCart()
        storage.updateLastUpdated(updatedCart.lastUpdated)
        Napier.d("[CartSync] clear: обновлен lastUpdated=${updatedCart.lastUpdated}")
    }

    override suspend fun sync() {
        Napier.d("[CartSync] ========== НАЧАЛО СИНХРОНИЗАЦИИ ==========")
        try {
            // Получаем локальную корзину и lastUpdated
            val localCart = storage.getCartItems()
            val localLastUpdated = storage.getLastUpdated()
            Napier.d("[CartSync] sync: получена локальная корзина, элементов=${localCart.size}, lastUpdated=$localLastUpdated")
            localCart.forEach { item ->
                Napier.d("[CartSync] sync: локальный элемент id=${item.id}, mealId=${item.mealId}, quantity=${item.quantity}, createdAt=${item.createdAt}, updatedAt=${item.updatedAt}")
            }

            // Получаем удалённую корзину с сервера
            val remoteCart = remoteDataSource.getCart()
            Napier.d("[CartSync] sync: получена удаленная корзина, элементов=${remoteCart.items.size}, lastUpdated=${remoteCart.lastUpdated}")
            remoteCart.items.forEach { item ->
                Napier.d("[CartSync] sync: удаленный элемент id=${item.id}, mealId=${item.mealId}, quantity=${item.quantity}, createdAt=${item.createdAt}, updatedAt=${item.updatedAt}")
            }

            // Объединяем локальную и удалённую корзину по updatedAt каждого элемента
            val mergedCart = mergeCartItems(localCart, remoteCart.items)
            Napier.d("[CartSync] sync: результат мержа, элементов=${mergedCart.size}")

            // Сохраняем объединённый результат локально
            Napier.d("[CartSync] sync: сохранение объединенной корзины в storage")
            mergedCart.forEach { item ->
                Napier.d("[CartSync] sync: сохранение элемента id=${item.id}, quantity=${item.quantity}, createdAt=${item.createdAt}, updatedAt=${item.updatedAt}")
                storage.addOrUpdateItem(item)
            }
            
            // Обновляем lastUpdated (берем максимальный из локального и удаленного)
            val finalLastUpdated = maxOf(localLastUpdated, remoteCart.lastUpdated)
            storage.updateLastUpdated(finalLastUpdated)
            Napier.d("[CartSync] sync: обновлен lastUpdated=$finalLastUpdated")

            // Обновляем UI
            updateUIFromStorage()
            
            Napier.d("[CartSync] ========== КОНЕЦ СИНХРОНИЗАЦИИ (успешно) ==========")
        } catch (e: Exception) {
            // В случае ошибки просто игнорируем синхронизацию
            // Локальные данные остаются без изменений
            Napier.e("[CartSync] ========== ОШИБКА СИНХРОНИЗАЦИИ ==========", e)
            Napier.e("[CartSync] sync: Exception при синхронизации", e)
        }
    }

    /**
     * Объединяет локальную и удалённую корзину.
     * Если есть дубликаты (одинаковые по id), берёт версию с более свежим updatedAt.
     * 
     * createdAt - время создания записи (не изменяется)
     * updatedAt - время последнего изменения позиции (используется для разрешения конфликтов)
     */
    private fun mergeCartItems(
        local: List<StoredCartItem>,
        remote: List<StoredCartItem>
    ): List<StoredCartItem> {
        Napier.d("[CartSync] mergeCartItems: начало объединения, локальных=${local.size}, удаленных=${remote.size}")
        // Создаём map для быстрого поиска по id
        val mergedMap = mutableMapOf<String, StoredCartItem>()

        // Добавляем локальные элементы
        var localAdded = 0
        local.forEach { item ->
            Napier.d("[CartSync] mergeCartItems: добавление локального элемента id=${item.id}, quantity=${item.quantity}, createdAt=${item.createdAt}, updatedAt=${item.updatedAt}")
            mergedMap[item.id] = item
            localAdded++
        }
        Napier.d("[CartSync] mergeCartItems: добавлено локальных элементов: $localAdded")

        // Добавляем удалённые элементы, при конфликте берём версию с более свежим updatedAt
        var remoteAdded = 0
        var conflictsResolved = 0
        remote.forEach { remoteItem ->
            Napier.d("[CartSync] mergeCartItems: обработка удаленного элемента id=${remoteItem.id}, quantity=${remoteItem.quantity}, createdAt=${remoteItem.createdAt}, updatedAt=${remoteItem.updatedAt}")
            val existing = mergedMap[remoteItem.id]
            if (existing == null) {
                // Такого элемента ещё нет, добавляем
                Napier.d("[CartSync] mergeCartItems: новый элемент добавлен id=${remoteItem.id}, quantity=${remoteItem.quantity}")
                mergedMap[remoteItem.id] = remoteItem
                remoteAdded++
            } else {
                // Есть дубликат, проверяем updatedAt
                conflictsResolved++
                Napier.d("[CartSync] mergeCartItems: конфликт для id=${remoteItem.id}, локальный: quantity=${existing.quantity}, updatedAt=${existing.updatedAt}, удаленный: quantity=${remoteItem.quantity}, updatedAt=${remoteItem.updatedAt}")
                
                // Сравниваем по updatedAt (время последнего изменения)
                if (remoteItem.updatedAt > existing.updatedAt) {
                    // Удаленная версия новее - используем её
                    Napier.d("[CartSync] mergeCartItems: конфликт разрешён в пользу удалённой версии id=${remoteItem.id}, updatedAt удаленного=${remoteItem.updatedAt} > локального=${existing.updatedAt}")
                    mergedMap[remoteItem.id] = remoteItem
                } else {
                    // Локальная версия новее или равна - сохраняем локальную
                    Napier.d("[CartSync] mergeCartItems: конфликт разрешён в пользу локальной версии id=${existing.id}, updatedAt локального=${existing.updatedAt} >= удаленного=${remoteItem.updatedAt}")
                }
            }
        }
        Napier.d("[CartSync] mergeCartItems: статистика - добавлено удалённых=$remoteAdded, разрешено конфликтов=$conflictsResolved")

        val result = mergedMap.values.toList()
        Napier.d("[CartSync] mergeCartItems: итоговое количество элементов: ${result.size}")
        result.forEach { item ->
            Napier.d("[CartSync] mergeCartItems: итоговый элемент id=${item.id}, quantity=${item.quantity}, createdAt=${item.createdAt}, updatedAt=${item.updatedAt}")
        }
        return result
    }
    
    private suspend fun updateUIFromStorage() {
        val currentCart = storage.getCartItems()
        val menuResource = menuCache.allVisibleMenu.value
        if (menuResource is Resource.Success) {
            val fullMenu = menuResource.data.orEmpty()
            val validItems = mapAndValidate(currentCart, fullMenu)
            cartItems = currentCart.mapNotNull { storedItem ->
                val baseMeal = flattenMeals(fullMenu).find { it.id == storedItem.mealId } ?: return@mapNotNull null
                try {
                    val adds = storedItem.addsIds.mapNotNull { addId ->
                        flattenMeals(fullMenu).find { it.id == addId }?.toMealAdditional()
                    }
                    val mods = storedItem.modifiers.validateBy(baseMeal.modifiers)
                    val customizedMeal = storedItem.toCustomizedMeal(baseMeal, adds, mods)
                    CartItem(
                        id = storedItem.id,
                        customizedMeal = customizedMeal,
                        quantity = storedItem.quantity,
                        comment = storedItem.comment
                    )
                } catch (e: Exception) {
                    Napier.e("[CartSync] Mapping failed for item in updateUIFromStorage: $storedItem", e)
                    null
                }
            }
            _cartItems.value = Resource.Success(validItems)
            _cartCount.value = validItems.sumOf { it.quantity }
            Napier.d("[CartSync] updateUIFromStorage: обновлено состояние UI, валидных элементов=${validItems.size}, общее количество=${_cartCount.value}")
        }
    }
}