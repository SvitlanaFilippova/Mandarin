package com.mandarinkafe.mandarin.features.cart.data.models

data class CartMetadata(
    val updatedAt: Long = 0L, // Время последнего изменения корзины
    val isDeleted: Boolean = false, // Флаг удаления корзины
)

