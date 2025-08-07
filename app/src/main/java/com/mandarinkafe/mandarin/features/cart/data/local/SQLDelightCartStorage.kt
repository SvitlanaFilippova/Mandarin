package com.mandarinkafe.mandarin.features.cart.data.local

import android.util.Log
import com.mandarinkafe.mandarin.db.CartItemsQueries
import com.mandarinkafe.mandarin.features.cart.data.CartMapper.toParams
import com.mandarinkafe.mandarin.features.cart.data.CartMapper.toStoredCartItem
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SQLDelightCartStorage @Inject constructor(private val queries: CartItemsQueries) :
    CartStorage {
    override suspend fun getCartItems(): List<StoredCartItem> {
        return try {
            withContext(Dispatchers.IO) {
                queries.selectAll()
                    .executeAsList()
                    .map { it.toStoredCartItem() }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.IO) {
                queries.deleteAll()
            }
            Log.d("Error CartStorage", "Ошибка при получении корзины из БД. Очищаю корзину. $e")
            emptyList()
        }
    }

    override suspend fun clearCart() {
        queries.deleteAll()
    }

    override suspend fun addOrUpdateItem(item: StoredCartItem) {
        val existingItem = queries.selectById(item.id).executeAsOneOrNull()
        val timestamp = existingItem?.timestamp ?: System.currentTimeMillis()

        val params = item.toParams()
        with(params) {
            queries.insertOrReplace(
                id = id,
                name = name,
                mealId = mealId,
                addsIds = addsJson,
                modifiers = modifiersJson,
                quantity = quantity,
                comment = comment,
                timestamp = timestamp
            )
        }
    }

    override suspend fun deleteItemById(id: String) {
        queries.deleteById(id)
    }
}