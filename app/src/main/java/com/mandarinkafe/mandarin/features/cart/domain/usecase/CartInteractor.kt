package com.mandarinkafe.mandarin.features.cart.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal

interface CartInteractor {
    suspend fun getCart(): Map<CustomizedMeal, Int>
    fun addToCart(item: CustomizedMeal)
    fun removeFromCart(item: CustomizedMeal)
    fun clearCart()
    suspend fun getCommonRecommends(): List<Meal>
}