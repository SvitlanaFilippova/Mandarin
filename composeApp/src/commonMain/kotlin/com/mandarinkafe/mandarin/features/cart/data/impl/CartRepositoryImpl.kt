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
import com.mandarinkafe.mandarin.features.cart.data.validateBy
import com.mandarinkafe.mandarin.features.cart.domain.api.CartWriter
import com.mandarinkafe.mandarin.features.menu.domain.toMealAdditional
import com.mandarinkafe.mandarin.util.Constants.MENU_WAIT_TIMEOUT
import com.mandarinkafe.mandarin.util.Resource
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
        var wasUpdated = false
        cartItems = cartItems.toMutableList().apply {
            val index = indexOfFirst { it.id == item.id }
            if (index != -1) {
                val oldItem = this[index]
                if (oldItem != item) {
                    this[index] = item
                    wasUpdated = true
                }
            } else {
                add(item)
            }
        }

        // Мгновенное обновление UI
        _cartItems.value = Resource.Success(cartItems)
        _cartCount.value = cartItems.sumOf { it.quantity }

        // Фоновое сохранение
        scope.launch(Dispatchers.Default) {
            storage.addOrUpdateItem(item.toStoredCartItem())

        }
        return wasUpdated
    }

    override suspend fun deleteItemById(id: String) {
        cartItems = cartItems.filterNot { it.id == id }
        _cartItems.value = Resource.Success(cartItems)
        _cartCount.value = cartItems.sumOf { it.quantity }
        scope.launch(Dispatchers.Default) {
            storage.deleteItemById(id)
        }

    }

    override suspend fun clear() {
        cartItems = emptyList()
        _cartItems.value = Resource.Success(emptyList())
        _cartCount.value = 0

        scope.launch(Dispatchers.Default) {
            storage.clearCart()
        }
    }
}