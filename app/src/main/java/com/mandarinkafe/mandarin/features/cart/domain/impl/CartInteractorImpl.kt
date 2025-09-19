package com.mandarinkafe.mandarin.features.cart.domain.impl

import com.mandarinkafe.mandarin.core.data.api.CartReader
import com.mandarinkafe.mandarin.core.domain.api.ForceRefreshMenuUseCase
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.equalsByContent
import com.mandarinkafe.mandarin.core.domain.models.isCustomized
import com.mandarinkafe.mandarin.features.cart.data.CartMapper.toCartItem
import com.mandarinkafe.mandarin.features.cart.domain.api.CartWriter
import com.mandarinkafe.mandarin.features.cart.domain.model.MealAddResult
import com.mandarinkafe.mandarin.features.cart.domain.usecase.CartInteractor
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.first

class CartInteractorImpl(
    private val cartWriter: CartWriter,
    private val cartReader: CartReader,
    private val forceRefreshMenu: ForceRefreshMenuUseCase,
) : CartInteractor {
    override fun observeCartItemsCount() = cartReader.observeCartItemsCount()
    override fun observeCartItems() = cartReader.observeCartItems()

    private suspend fun getCurrentCartItems(): List<CartItem> {
        val result = cartReader.observeCartItems().first()
        return when (result) {
            is Resource.Success -> result.data ?: emptyList()
            else -> emptyList()
        }
    }

    override suspend fun forceRefresh() {
        forceRefreshMenu()
        cartReader.forceRetry()
    }

    override suspend fun addItem(
        cartItem: CartItem?,
        customizedMeal: CustomizedMeal?,
        meal: Meal?
    ) {
        when {
            cartItem != null -> addOrIncrement(cartItem)
            customizedMeal != null -> addOrIncrement(customizedMeal.toCartItem())
            meal != null -> addOrIncrement(meal.toCartItem())
            else -> Unit
        }
    }

    private suspend fun addOrIncrement(target: CartItem) {
        val currentItems = getCurrentCartItems()

        val existing = currentItems.find { it.equalsByContent(target) }
        if (existing != null) {
            val updated = existing.copy(quantity = existing.quantity + 1)
            cartWriter.addOrUpdateItem(updated)
        } else {
            cartWriter.addOrUpdateItem(target)
        }
    }

    override suspend fun updateItem(newCartItem: CartItem, oldItem: CartItem?): Boolean {
        val itemToUpdate = oldItem?.copy(
            customizedMeal = newCartItem.customizedMeal,
            comment = newCartItem.comment
        ) ?: newCartItem
        return cartWriter.addOrUpdateItem(itemToUpdate)
    }

    override suspend fun tryAddMeal(cartItem: CartItem): MealAddResult {
        val currentItems = getCurrentCartItems()
        // Базовое блюдо (не кастомизированное, без комментария)
        if (!cartItem.customizedMeal.isCustomized && cartItem.comment.isEmpty()) {
            val existingBase = currentItems.find {
                !it.customizedMeal.isCustomized &&
                        it.comment.isEmpty() &&
                        it.customizedMeal.meal.id == cartItem.customizedMeal.meal.id
            }
            if (existingBase != null) {
                // просто увеличиваем количество
                val updated =
                    existingBase.copy(quantity = existingBase.quantity + 1)
                cartWriter.addOrUpdateItem(updated)
                return MealAddResult.Added
            }
        }
        //  Кастомизированное блюдо, которое совпадает по базе с обычным
        if (cartItem.customizedMeal.isCustomized) {
            val existingBaseMeal = currentItems.find {
                it.customizedMeal.meal.id == cartItem.customizedMeal.meal.id &&
                        !it.customizedMeal.isCustomized && it.comment.isEmpty()
            }
            if (existingBaseMeal != null) {
                return MealAddResult.AlreadyExistBaseMeal(existingBaseMeal)
            }
        }

        // В корзине ничего похожего нет — добавляем
        cartWriter.addOrUpdateItem(cartItem)
        return MealAddResult.Added
    }

    override suspend fun removeFromCart(
        cartItemId: String?,
        customizedMeal: CustomizedMeal?,
        meal: Meal?
    ) {
        val currentItems = getCurrentCartItems()
        val target: CartItem? = when {
            cartItemId != null -> currentItems.find { it.id == cartItemId }
            customizedMeal != null -> currentItems.lastOrNull { it.customizedMeal == customizedMeal }
            meal != null -> currentItems.lastOrNull { it.customizedMeal.meal == meal }
            else -> null
        }

        target?.let { decrementOrRemove(it) }
    }

    private suspend fun decrementOrRemove(item: CartItem) {
        if (item.quantity > 1) {
            cartWriter.addOrUpdateItem(item.copy(quantity = item.quantity - 1))
        } else {
            cartWriter.deleteItemById(item.id)
        }
    }

    override suspend fun clearCart() {
        cartWriter.clear()
    }
}