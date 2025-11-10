package com.mandarinkafe.mandarin.features.cart.data.impl

import com.mandarinkafe.mandarin.core.domain.api.CartReader
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.auth.domain.impl.AuthStateChecker
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
    private val authStateChecker: AuthStateChecker,
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
                Napier.e("Ошибка при чтении корзины из storage", e)
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
                Napier.e("Mapping failed for item: $item", e)
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
        // Шаг 1: Синхронизация с сервером для получения актуальной версии
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
        storage.addOrUpdateItem(storedItem)

        // Обновляем UI из storage
        updateUIFromStorage()

        var wasUpdated = cartItems.any { it.id == item.id && it != item }
        if (!wasUpdated && existingStored == null) {
            wasUpdated = true // Новый элемент добавлен
        }

        // Шаг 3: Отправляем корзину на сервер и получаем обновленную версию с updatedAt
        val localCart = storage.getCartItems()
        val updatedCart = remoteDataSource.syncCart(localCart)

        // Сохраняем обновленную корзину с updatedAt от сервера
        updatedCart.items.forEach { updatedItem ->
            storage.addOrUpdateItem(updatedItem)
        }
        storage.updateLastUpdated(updatedCart.lastUpdated)

        // Обновляем UI
        updateUIFromStorage()

        return wasUpdated
    }

    override suspend fun deleteItemById(id: String) = mutex.withLock {
        // Шаг 1: Синхронизация с сервером для получения актуальной версии
        sync()

        // Шаг 2: Применяем локальное изменение - физически удаляем элемент
        storage.deleteItemById(id)

        // Обновляем UI из storage
        updateUIFromStorage()

        // Шаг 3: Отправляем корзину на сервер и получаем обновленную версию с updatedAt
        val localCart = storage.getCartItems()
        val updatedCart = remoteDataSource.syncCart(localCart)

        // Сохраняем обновленную корзину с updatedAt от сервера
        updatedCart.items.forEach { updatedItem ->
            storage.addOrUpdateItem(updatedItem)
        }
        storage.updateLastUpdated(updatedCart.lastUpdated)

        // Обновляем UI
        updateUIFromStorage()
    }

    override suspend fun clear() = mutex.withLock {
        // Шаг 1: Синхронизация с сервером для получения актуальной версии
        sync()

        // Шаг 2: Применяем локальное изменение - очищаем корзину
        storage.clearCart()

        // Обновляем UI
        cartItems = emptyList()
        _cartItems.value = Resource.Success(emptyList())
        _cartCount.value = 0

        // Шаг 3: Отправляем DELETE /cart на сервер
        if (authStateChecker.isAuthorizedFast()) {
            remoteDataSource.clearCart()
        }

        // Получаем обновленную корзину (должна быть пустой) и обновляем lastUpdated
        val updatedCart = remoteDataSource.getCart()
        storage.updateLastUpdated(updatedCart.lastUpdated)
    }

    override suspend fun sync() {
        // Синхронизируем только если пользователь авторизован
        if (!authStateChecker.isAuthorizedFast()) {
            return
        }

        try {
            val localCart = storage.getCartItems()
            val localLastUpdated = storage.getLastUpdated()
            val remoteCart = remoteDataSource.getCart()

            if (shouldClearLocalCart(remoteCart, localLastUpdated)) {
                handleServerClearedCart(remoteCart)
            } else {
                performCartMerge(localCart, localLastUpdated, remoteCart)
            }

            updateUIFromStorage()
        } catch (e: Exception) {
            Napier.e("Ошибка при синхронизации корзины", e)
        }
    }

    private fun shouldClearLocalCart(
        remoteCart: CartMetadata,
        localLastUpdated: Long,
    ): Boolean {
        return remoteCart.items.isEmpty() && remoteCart.lastUpdated > localLastUpdated
    }

    private suspend fun handleServerClearedCart(
        remoteCart: CartMetadata,
    ) {
        storage.clearCart()
        storage.updateLastUpdated(remoteCart.lastUpdated)
    }

    private suspend fun performCartMerge(
        localCart: List<StoredCartItem>,
        localLastUpdated: Long,
        remoteCart: CartMetadata,
    ) {
        val isFirstSyncAfterAuth = isFirstSyncAfterAuthorization(localLastUpdated, localCart)
        var mergedCart = mergeCartItems(localCart, remoteCart.items, isFirstSyncAfterAuth)

        val serverIsNewerOrEqual = remoteCart.lastUpdated >= localLastUpdated
        mergedCart = removeItemsNotOnServer(
            mergedCart,
            remoteCart.items,
            serverIsNewerOrEqual,
            isFirstSyncAfterAuth
        )

        val hasLocalChanges = checkForLocalChanges(
            localCart,
            remoteCart.items,
            serverIsNewerOrEqual,
            isFirstSyncAfterAuth
        )

        saveMergedCart(
            mergedCart,
            localCart,
            remoteCart.items,
            serverIsNewerOrEqual,
            isFirstSyncAfterAuth
        )
        updateLastUpdatedAndSyncIfNeeded(localLastUpdated, remoteCart.lastUpdated, hasLocalChanges)
    }

    private fun isFirstSyncAfterAuthorization(
        localLastUpdated: Long,
        localCart: List<StoredCartItem>,
    ): Boolean {
        return localLastUpdated == 0L && localCart.isNotEmpty() && localCart.any { it.updatedAt == 0L }
    }

    private fun removeItemsNotOnServer(
        mergedCart: List<StoredCartItem>,
        remoteItems: List<StoredCartItem>,
        serverIsNewerOrEqual: Boolean,
        isFirstSyncAfterAuth: Boolean,
    ): List<StoredCartItem> {
        if (serverIsNewerOrEqual && !isFirstSyncAfterAuth) {
            val remoteItemIds = remoteItems.map { it.id }.toSet()
            return mergedCart.filter { it.id in remoteItemIds }
        }
        return mergedCart
    }

    private fun checkForLocalChanges(
        localCart: List<StoredCartItem>,
        remoteItems: List<StoredCartItem>,
        serverIsNewerOrEqual: Boolean,
        isFirstSyncAfterAuth: Boolean,
    ): Boolean {
        if (isFirstSyncAfterAuth) return true

        return localCart.any { localItem ->
            val remoteItem = remoteItems.find { it.id == localItem.id }
            when {
                remoteItem == null -> !serverIsNewerOrEqual
                localItem.updatedAt == 0L -> true
                localItem.updatedAt > remoteItem.updatedAt -> true
                else -> false
            }
        }
    }

    private suspend fun saveMergedCart(
        mergedCart: List<StoredCartItem>,
        localCart: List<StoredCartItem>,
        remoteItems: List<StoredCartItem>,
        serverIsNewerOrEqual: Boolean,
        isFirstSyncAfterAuth: Boolean,
    ) {
        mergedCart.forEach { item ->
            storage.addOrUpdateItem(item)
        }

        if (serverIsNewerOrEqual && !isFirstSyncAfterAuth) {
            val remoteItemIds = remoteItems.map { it.id }.toSet()
            val localItemIds = localCart.map { it.id }.toSet()
            val itemsToDeleteFromStorage = localItemIds - remoteItemIds
            itemsToDeleteFromStorage.forEach { id ->
                storage.deleteItemById(id)
            }
        }
    }

    private suspend fun updateLastUpdatedAndSyncIfNeeded(
        localLastUpdated: Long,
        remoteLastUpdated: Long,
        hasLocalChanges: Boolean,
    ) {
        val finalLastUpdated = maxOf(localLastUpdated, remoteLastUpdated)
        storage.updateLastUpdated(finalLastUpdated)

        if (hasLocalChanges) {
            val currentCart = storage.getCartItems()
            val updatedCart = remoteDataSource.syncCart(currentCart)

            updatedCart.items.forEach { updatedItem ->
                storage.addOrUpdateItem(updatedItem)
            }
            storage.updateLastUpdated(updatedCart.lastUpdated)
        }
    }

    /**
     * Объединяет локальную и удалённую корзину.
     * Если есть дубликаты (одинаковые по id), берёт версию с более свежим updatedAt.
     * При первой синхронизации после авторизации локальные элементы с updatedAt = 0L имеют приоритет.
     *
     * createdAt - время создания записи (не изменяется)
     * updatedAt - время последнего изменения позиции (используется для разрешения конфликтов)
     */
    private fun mergeCartItems(
        local: List<StoredCartItem>,
        remote: List<StoredCartItem>,
        isFirstSyncAfterAuth: Boolean = false,
    ): List<StoredCartItem> {
        // Создаём map для быстрого поиска по id
        val mergedMap = mutableMapOf<String, StoredCartItem>()

        // Добавляем локальные элементы
        local.forEach { item ->
            mergedMap[item.id] = item
        }

        // Добавляем удалённые элементы, при конфликте берём версию с более свежим updatedAt
        remote.forEach { remoteItem ->
            val existing = mergedMap[remoteItem.id]
            if (existing == null) {
                // Такого элемента ещё нет, добавляем
                mergedMap[remoteItem.id] = remoteItem
            } else {
                // Есть дубликат, проверяем updatedAt
                // При первой синхронизации локальные элементы с updatedAt = 0L имеют приоритет
                if (isFirstSyncAfterAuth && existing.updatedAt == 0L) {
                    // Локальный элемент создан без авторизации - сохраняем его для отправки на сервер
                    // Не заменяем серверной версией
                } else if (remoteItem.updatedAt > existing.updatedAt) {
                    // Удаленная версия новее - используем её
                    mergedMap[remoteItem.id] = remoteItem
                }
                // Иначе локальная версия новее или равна - сохраняем локальную (уже в map)
            }
        }

        return mergedMap.values.toList()
    }

    private suspend fun updateUIFromStorage() {
        val currentCart = storage.getCartItems()
        val menuResource = menuCache.allVisibleMenu.value
        if (menuResource is Resource.Success) {
            val fullMenu = menuResource.data.orEmpty()
            val validItems = mapAndValidate(currentCart, fullMenu)
            cartItems = currentCart.mapNotNull { storedItem ->
                val baseMeal = flattenMeals(fullMenu).find { it.id == storedItem.mealId }
                    ?: return@mapNotNull null
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
                    Napier.e("Mapping failed for item in updateUIFromStorage: $storedItem", e)
                    null
                }
            }
            _cartItems.value = Resource.Success(validItems)
            _cartCount.value = validItems.sumOf { it.quantity }
        }
    }
}