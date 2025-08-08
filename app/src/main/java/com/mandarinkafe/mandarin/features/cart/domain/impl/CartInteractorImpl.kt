package com.mandarinkafe.mandarin.features.cart.domain.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.api.CartReader
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.cart.data.CartMapper.toCartItem
import com.mandarinkafe.mandarin.features.cart.domain.api.CartWriter
import com.mandarinkafe.mandarin.features.cart.domain.usecase.CartInteractor
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.first

class CartInteractorImpl(
    private val cartWriter: CartWriter,
    private val cartReader: CartReader, // чтобы получить текущие cartItems
) : CartInteractor {
    override fun observeCartItemsCount() = cartReader.observeCartItemsCount()
    val logTag = "CART DEBUG Interactor"
    override fun observeCartItems() = cartReader.observeCartItems()

    private suspend fun getCurrentCartItems(): List<CartItem> {
        val result = cartReader.observeCartItems().first()
        return when (result) {
            is Resource.Success -> result.data ?: emptyList()
            else -> emptyList()
        }
    }

    override suspend fun addItem(
        cartItem: CartItem?,
        customizedMeal: CustomizedMeal?,
        meal: Meal?
    ) {
        when {
            cartItem != null -> {
                // Если пришёл готовый CartItem, ищем его по id и обновляем/добавляем
                val existing = getCurrentCartItems().find { it.id == cartItem.id }
                if (existing != null) {
                    // увеличиваем количество
                    val updated = existing.copy(quantity = existing.quantity + 1)
                    cartWriter.addOrUpdateItem(updated)
                } else {
                    cartWriter.addOrUpdateItem(cartItem)
                }
            }

            customizedMeal != null -> {
                // Ищем последний CartItem с таким же customizedMeal
                val existing =
                    getCurrentCartItems().lastOrNull { it.customizedMeal == customizedMeal }
                if (existing != null) {
                    val updated = existing.copy(quantity = existing.quantity + 1)
                    cartWriter.addOrUpdateItem(updated)
                } else {
                    val newItem = customizedMeal.toCartItem()
                    cartWriter.addOrUpdateItem(newItem)
                }
            }

            meal != null -> {
                // Ищем последний CartItem с таким meal внутри customizedMeal
                val existing = getCurrentCartItems().lastOrNull { it.customizedMeal.meal == meal }
                if (existing != null) {
                    val updated = existing.copy(quantity = existing.quantity + 1)
                    cartWriter.addOrUpdateItem(updated)
                } else {
                    val newItem = meal.toCartItem()
                    cartWriter.addOrUpdateItem(newItem)
                }
            }

            else -> {
                // Все параметры null — ничего не делаем
            }
        }
    }

    override suspend fun updateItem(cartItem: CartItem) {
        // здесь ожидается уже корректное количество quantity, обновляем только данные
        cartWriter.addOrUpdateItem(cartItem)
    }

    override suspend fun removeFromCart(
        cartItemId: String?,
        customizedMeal: CustomizedMeal?,
        meal: Meal?
    ) {
        Log.d(
            logTag,
            "removeFromCart() called: cartItemId = $cartItemId, meal = $meal, customizedMeal = $customizedMeal"
        )

        when {
            cartItemId != null -> {
                // Для CartItem — удаляем сразу
                cartWriter.deleteItemById(cartItemId)
            }

            customizedMeal != null -> {
                val currentItems = getCurrentCartItems()
                val target = currentItems.lastOrNull { it.customizedMeal == customizedMeal }
                if (target != null) {
                    if (target.quantity > 1) {
                        // Уменьшаем количество
                        val updated = target.copy(quantity = target.quantity - 1)
                        cartWriter.addOrUpdateItem(updated)
                    } else {
                        // Удаляем полностью
                        cartWriter.deleteItemById(target.id)
                    }
                }
            }

            meal != null -> {
                val currentItems = getCurrentCartItems()
                val target = currentItems.lastOrNull { it.customizedMeal.meal == meal }
                if (target != null) {
                    if (target.quantity > 1) {
                        val updated = target.copy(quantity = target.quantity - 1)
                        cartWriter.addOrUpdateItem(updated)
                    } else {
                        cartWriter.deleteItemById(target.id)
                    }
                }
            }

            else -> {
                // Ничего не передано — ничего не делаем
            }
        }
    }

    override suspend fun clearCart() {
        cartWriter.clear()
    }
}