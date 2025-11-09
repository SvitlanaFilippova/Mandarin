package com.mandarinkafe.mandarin.features.cart.data.impl

import com.mandarinkafe.mandarin.core.domain.api.CartReader
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.cart.data.Mapper.toCustomizedMeal
import com.mandarinkafe.mandarin.features.cart.data.Mapper.toStoredCartItem
import com.mandarinkafe.mandarin.features.cart.data.local.CartStorage
import com.mandarinkafe.mandarin.features.cart.data.models.CartMetadata
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
            // Пропускаем элементы с quantity=0 (удаленные)
            if (item.quantity == 0) {
                continue
            }
            
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
        // Проверяем, существует ли элемент в storage после синхронизации
        val existingStored = storage.getCartItems().find { it.id == item.id }
        val timestamp = if (existingStored == null) {
            // Новый элемент - устанавливаем timestamp
            getCurrentTimeMillis()
        } else {
            // Существующий элемент - сохраняем старый timestamp
            existingStored.timestamp
        }
        Napier.d("[CartSync] addOrUpdateItem: сохранение в storage, id=${item.id}, timestamp=$timestamp")
        storage.addOrUpdateItem(item.toStoredCartItem(timestamp))
        
        // Обновляем UI из storage
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
                    null
                }
            }
            _cartItems.value = Resource.Success(validItems)
            _cartCount.value = validItems.sumOf { it.quantity }
        }
        
        var wasUpdated = cartItems.any { it.id == item.id && it != item }
        if (!wasUpdated && existingStored == null) {
            wasUpdated = true // Новый элемент добавлен
        }
        
        // Шаг 3: Синхронизация с сервером для отправки итоговой версии
        Napier.d("[CartSync] addOrUpdateItem: синхронизация с сервером после изменения")
        sync()
        
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
                    null
                }
            }
            _cartItems.value = Resource.Success(validItems)
            _cartCount.value = validItems.sumOf { it.quantity }
        }
        
        // Шаг 3: Синхронизация с сервером для отправки итоговой версии
        Napier.d("[CartSync] deleteItemById: синхронизация с сервером после удаления")
        sync()
    }

    override suspend fun clear() = mutex.withLock {
        Napier.d("[CartSync] clear: начало очистки корзины")
        
        // Шаг 1: Синхронизация с сервером для получения актуальной версии
        Napier.d("[CartSync] clear: синхронизация с сервером перед очисткой")
        sync()
        
        // Шаг 2: Применяем локальное изменение - очищаем корзину
        storage.clearCart()
        // Устанавливаем метаданные: isDeleted=true, updatedAt=текущее время
        val currentTime = getCurrentTimeMillis()
        storage.updateCartMetadata(CartMetadata(updatedAt = currentTime, isDeleted = true))
        Napier.d("[CartSync] clear: корзина очищена, updatedAt=$currentTime, isDeleted=true")
        
        // Обновляем UI
        cartItems = emptyList()
        _cartItems.value = Resource.Success(emptyList())
        _cartCount.value = 0
        
        // Шаг 3: Синхронизация с сервером для отправки итоговой версии (используем DELETE /cart)
        Napier.d("[CartSync] clear: отправка DELETE /cart на сервер")
        remoteDataSource.clearCart()
        
        // Также обновляем метаданные на сервере через sync
        sync()
    }

    override suspend fun sync() {
        Napier.d("[CartSync] ========== НАЧАЛО СИНХРОНИЗАЦИИ ==========")
        try {
            // Получаем локальную корзину и метаданные
            val localCart = storage.getCartItems()
            val localMetadata = storage.getCartMetadata() ?: CartMetadata(updatedAt = 0L, isDeleted = false)
            Napier.d("[CartSync] sync: получена локальная корзина, элементов=${localCart.size}, updatedAt=${localMetadata.updatedAt}, isDeleted=${localMetadata.isDeleted}")
            localCart.forEach { item ->
                Napier.d("[CartSync] sync: локальный элемент id=${item.id}, mealId=${item.mealId}, quantity=${item.quantity}, timestamp=${item.timestamp}")
            }

            // Получаем удалённую корзину с сервера
            val remoteCart = remoteDataSource.getCart()
            Napier.d("[CartSync] sync: получена удаленная корзина, элементов=${remoteCart.items.size}, updatedAt=${remoteCart.metadata.updatedAt}, isDeleted=${remoteCart.metadata.isDeleted}")
            remoteCart.items.forEach { item ->
                Napier.d("[CartSync] sync: удаленный элемент id=${item.id}, mealId=${item.mealId}, quantity=${item.quantity}, timestamp=${item.timestamp}")
            }

            // Сравниваем метаданные корзины для определения актуальной версии
            val shouldUseRemote = when {
                remoteCart.metadata.isDeleted && !localMetadata.isDeleted -> {
                    // Удаленная корзина помечена как удаленная, локальная - нет
                    remoteCart.metadata.updatedAt >= localMetadata.updatedAt
                }
                !remoteCart.metadata.isDeleted && localMetadata.isDeleted -> {
                    // Локальная корзина помечена как удаленная, удаленная - нет
                    localMetadata.updatedAt >= remoteCart.metadata.updatedAt
                }
                else -> {
                    // Обе корзины в одинаковом состоянии (обе удалены или обе активны)
                    // Используем ту, у которой более свежий updatedAt
                    remoteCart.metadata.updatedAt > localMetadata.updatedAt
                }
            }
            
            Napier.d("[CartSync] sync: сравнение метаданных - использовать удаленную=$shouldUseRemote (локальная: updatedAt=${localMetadata.updatedAt}, isDeleted=${localMetadata.isDeleted}, удаленная: updatedAt=${remoteCart.metadata.updatedAt}, isDeleted=${remoteCart.metadata.isDeleted})")

            // Объединяем локальную и удалённую корзину
            // Если есть дубликаты (одинаковые по id), берём версию с более свежим timestamp
            val mergedCart = mergeCartItems(localCart, remoteCart.items)
            Napier.d("[CartSync] sync: результат мержа, элементов=${mergedCart.size}")

            // Определяем финальные метаданные корзины
            val finalMetadata = if (shouldUseRemote) {
                remoteCart.metadata
            } else {
                localMetadata
            }
            
            // Если удаленная корзина помечена как удаленная и она новее - очищаем локальную корзину
            if (shouldUseRemote && remoteCart.metadata.isDeleted) {
                Napier.d("[CartSync] sync: удаленная корзина помечена как удаленная и новее - очищаем локальную корзину")
                storage.clearCart()
            } else {
                // Сохраняем объединённый результат локально
                Napier.d("[CartSync] sync: сохранение объединенной корзины в storage")
                mergedCart.forEach { item ->
                    Napier.d("[CartSync] sync: сохранение элемента id=${item.id}, quantity=${item.quantity}, timestamp=${item.timestamp}")
                    storage.addOrUpdateItem(item)
                }
            }
            
            // Обновляем метаданные корзины
            storage.updateCartMetadata(finalMetadata)
            Napier.d("[CartSync] sync: обновлены метаданные корзины, updatedAt=${finalMetadata.updatedAt}, isDeleted=${finalMetadata.isDeleted}")

            // Обновляем внутреннее состояние
            val menuResource = menuCache.allVisibleMenu.value
            if (menuResource is Resource.Success) {
                val fullMenu = menuResource.data.orEmpty()
                // mapAndValidate уже фильтрует элементы с quantity=0
                val validItems = mapAndValidate(mergedCart, fullMenu)
                // Но для cartItems нужно сохранить все элементы, включая с quantity=0
                // для правильной работы addOrUpdateItem
                cartItems = mergedCart.mapNotNull { storedItem ->
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
                        Napier.e("[CartSync] Mapping failed for item in sync: $storedItem", e)
                        null
                    }
                }
                // В UI показываем только элементы с quantity > 0
                _cartItems.value = Resource.Success(validItems)
                _cartCount.value = validItems.sumOf { it.quantity }
                Napier.d("[CartSync] sync: обновлено состояние UI, валидных элементов=${validItems.size}, общее количество=${_cartCount.value}")
            }

            // Отправляем объединённый результат на сервер (только если локальная версия новее)
            if (!shouldUseRemote) {
                val finalCart = if (finalMetadata.isDeleted) emptyList() else mergedCart
                Napier.d("[CartSync] sync: отправка объединенной корзины на сервер, элементов=${finalCart.size}, updatedAt=${finalMetadata.updatedAt}, isDeleted=${finalMetadata.isDeleted}")
                remoteDataSource.syncCart(finalCart, finalMetadata)
            } else {
                Napier.d("[CartSync] sync: локальная версия старше, не отправляем на сервер")
            }
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
     * Если есть дубликаты (одинаковые по id), берёт версию с более свежим timestamp.
     * 
     * timestamp - время создания записи (не изменяется)
     * 
     * Элементы с quantity=0 считаются удаленными и не показываются в UI,
     * но сохраняются для синхронизации.
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
            Napier.d("[CartSync] mergeCartItems: добавление локального элемента id=${item.id}, quantity=${item.quantity}, timestamp=${item.timestamp}")
            mergedMap[item.id] = item
            localAdded++
        }
        Napier.d("[CartSync] mergeCartItems: добавлено локальных элементов: $localAdded")

        // Добавляем удалённые элементы, при конфликте берём версию с более свежим timestamp
        var remoteAdded = 0
        var conflictsResolved = 0
        remote.forEach { remoteItem ->
            Napier.d("[CartSync] mergeCartItems: обработка удаленного элемента id=${remoteItem.id}, quantity=${remoteItem.quantity}, timestamp=${remoteItem.timestamp}")
            val existing = mergedMap[remoteItem.id]
            if (existing == null) {
                // Такого элемента ещё нет, добавляем
                Napier.d("[CartSync] mergeCartItems: новый элемент добавлен id=${remoteItem.id}, quantity=${remoteItem.quantity}")
                mergedMap[remoteItem.id] = remoteItem
                remoteAdded++
            } else {
                // Есть дубликат, проверяем timestamp
                conflictsResolved++
                Napier.d("[CartSync] mergeCartItems: конфликт для id=${remoteItem.id}, локальный: quantity=${existing.quantity}, timestamp=${existing.timestamp}, удаленный: quantity=${remoteItem.quantity}, timestamp=${remoteItem.timestamp}")
                
                // Сравниваем по timestamp (время создания)
                if (remoteItem.timestamp > existing.timestamp) {
                    // Удаленная версия новее - используем её
                    Napier.d("[CartSync] mergeCartItems: конфликт разрешён в пользу удалённой версии id=${remoteItem.id}, timestamp удаленного=${remoteItem.timestamp} > локального=${existing.timestamp}")
                    mergedMap[remoteItem.id] = remoteItem
                } else {
                    // Локальная версия новее или равна - сохраняем локальную
                    Napier.d("[CartSync] mergeCartItems: конфликт разрешён в пользу локальной версии id=${existing.id}, timestamp локального=${existing.timestamp} >= удаленного=${remoteItem.timestamp}")
                }
            }
        }
        Napier.d("[CartSync] mergeCartItems: статистика - добавлено удалённых=$remoteAdded, разрешено конфликтов=$conflictsResolved")

        val result = mergedMap.values.toList()
        Napier.d("[CartSync] mergeCartItems: итоговое количество элементов: ${result.size}")
        result.forEach { item ->
            Napier.d("[CartSync] mergeCartItems: итоговый элемент id=${item.id}, quantity=${item.quantity}, timestamp=${item.timestamp}")
        }
        return result
    }
}