package com.mandarinkafe.mandarin.features.cart.data.models

data class CartMetadata(
    val items: List<StoredCartItem>,
    val lastUpdated: Long = 0L, // время последнего изменения всей корзины (задаётся на сервере, клиент только сравнивает для логики мержа)
)
