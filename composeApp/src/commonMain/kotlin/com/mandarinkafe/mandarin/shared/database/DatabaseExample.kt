package com.mandarinkafe.mandarin.shared.database

import kotlinx.datetime.Clock

/**
 * Пример использования базы данных SQLDelight
 * 
 * Этот файл демонстрирует, как правильно использовать базу данных
 * в Kotlin Multiplatform проекте с SQLDelight.
 */
class DatabaseExample(private val databaseProvider: DatabaseProvider) {
    
    /**
     * Пример добавления товара в корзину
     */
    suspend fun addItemToCart(
        id: String,
        name: String,
        mealId: String,
        quantity: Int,
        comment: String? = null
    ) {
        databaseProvider.cartItemsQueries.insertOrReplace(
            id = id,
            name = name,
            mealId = mealId,
            addsIds = "[]", // Пустой JSON массив
            modifiers = "{}", // Пустой JSON объект
            quantity = quantity.toLong(),
            comment = comment,
            timestamp = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        )
    }
    
    /**
     * Пример получения всех товаров из корзины
     */
    suspend fun getAllCartItems(): List<CartItem> {
        return databaseProvider.cartItemsQueries.selectAll()
            .executeAsList()
            .map { row ->
                CartItem(
                    id = row.id,
                    name = row.name,
                    mealId = row.mealId,
                    addsIds = row.addsIds,
                    modifiers = row.modifiers,
                    quantity = row.quantity,
                    comment = row.comment,
                    timestamp = row.timestamp
                )
            }
    }
    
    /**
     * Пример удаления товара из корзины
     */
    suspend fun removeItemFromCart(id: String) {
        databaseProvider.cartItemsQueries.deleteById(id)
    }
    
    /**
     * Пример очистки корзины
     */
    suspend fun clearCart() {
        databaseProvider.cartItemsQueries.deleteAll()
    }
}

/**
 * Модель данных для товара в корзине
 */
data class CartItem(
    val id: String,
    val name: String,
    val mealId: String,
    val addsIds: String,
    val modifiers: String,
    val quantity: Long,
    val comment: String?,
    val timestamp: Long
)
