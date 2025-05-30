package com.mandarinkafe.mandarin.shared.cart.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.util.Resource

interface CartInteractor {
    suspend fun getCart(): Resource<Map<CustomizedMeal, Int>>
    fun addToCart(item: CustomizedMeal)
    fun removeFromCart(item: CustomizedMeal)
    fun clearCart()
}