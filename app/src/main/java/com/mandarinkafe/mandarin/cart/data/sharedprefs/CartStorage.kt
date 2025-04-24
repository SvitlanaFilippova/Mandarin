package com.mandarinkafe.mandarin.cart.data.sharedprefs

import com.mandarinkafe.mandarin.cart.data.models.CartMeal

interface CartStorage {
    fun getCart(): List<CartMeal>
    fun clearCart()
    fun saveCart(items: List<CartMeal>)
}