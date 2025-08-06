package com.mandarinkafe.mandarin.features.cart.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.api.CartReader
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.cart.data.CartMapper.toCustomizedMeal
import com.mandarinkafe.mandarin.features.cart.data.CartMapper.toStoredCartItem
import com.mandarinkafe.mandarin.features.cart.data.local.CartStorage
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem
import com.mandarinkafe.mandarin.features.cart.data.validateBy
import com.mandarinkafe.mandarin.features.cart.domain.api.CartWriter
import com.mandarinkafe.mandarin.features.menu.domain.mappers.toMealAdditional
import com.mandarinkafe.mandarin.util.Resource
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val storage: CartStorage,
    private val menuCache: MenuCache,
) : CartWriter, CartReader {

    private val _cartCount = MutableStateFlow(0)
    override fun observeCartItemsCount(): Flow<Int> = _cartCount.asStateFlow()

    private val _cartItems = MutableStateFlow<Resource<List<CartItem>>>(Resource.Idle())
    override fun observeCartItems(): Flow<Resource<List<CartItem>>> = _cartItems.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        observeCartWithMenu()
    }

    private fun observeCartWithMenu() {
        scope.launch {
            combine(
                storage.observeCartItems(),
                menuCache.fullMenu
                    .filter { it !is Resource.Loading && it !is Resource.Idle }
            ) { rawCart, menuResource ->
                rawCart to menuResource
            }.collect { (rawCart, menuResource) ->
                _cartItems.value = when (menuResource) {
                    is Resource.Success -> {
                        val menu = menuResource.data.orEmpty()
                        val (validItems, _) = mapAndValidate(rawCart, menu)
                        _cartCount.value = validItems.sumOf { it.quantity }

                        if (validItems.isEmpty()) {
                            Resource.ErrorEmptyData()
                        } else {
                            Resource.Success(validItems)
                        }
                    }

                    is Resource.ErrorNoInternet -> {
                        _cartCount.value = 0
                        Resource.ErrorNoInternet()
                    }

                    is Resource.ErrorEmptyData -> {
                        _cartCount.value = 0
                        Resource.ErrorEmptyData()
                    }

                    is Resource.ErrorOther -> {
                        _cartCount.value = 0
                        Resource.ErrorOther(menuResource.message ?: "Unknown error")
                    }

                    is Resource.Loading -> {
                        Resource.Loading()
                    }

                    is Resource.Idle -> {
                        Resource.Idle()
                    }
                }
            }
        }
    }

    private fun mapAndValidate(
        raw: List<StoredCartItem>,
        menu: List<MealCategory>
    ): Pair<List<CartItem>, List<String>> {
        val valid = mutableListOf<CartItem>()
        val invalid = mutableListOf<String>()

        val allMeals = menu.flatMap { category ->
            category.meals.orEmpty() +
                    category.subCategories.orEmpty().flatMap { it.meals.orEmpty() }
        }.associateBy { it.id }

        for (item in raw) {
            val baseMeal = allMeals[item.mealId]
            if (baseMeal == null) {
                invalid += item.mealId
                continue
            }

            try {
                val adds = item.addsIds.mapNotNull { allMeals[it]?.toMealAdditional() }
                val mods = item.modifiers.validateBy(baseMeal.modifiers).orEmpty()
                val customizedMeal = item.toCustomizedMeal(baseMeal, adds, mods)

                valid += CartItem(
                    id = item.id,
                    name = item.name,
                    customizedMeal = customizedMeal,
                    quantity = item.quantity,
                    comment = item.comment
                )
            } catch (e: Exception) {
                Log.e(ERROR_TAG, "Mapping failed for item: $item", e)
                invalid += item.mealId
            }
        }

        return valid to invalid
    }

    override suspend fun addOrUpdateItem(item: CartItem) {
        storage.addOrUpdateItem(item.toStoredCartItem())
    }

    override suspend fun deleteItemById(id: String) {
        storage.deleteItemById(id)
    }

    override suspend fun clearCart() {
        storage.clearCart()
    }

    companion object {
        private const val ERROR_TAG = "Cart DEBUG Repo"
    }
}