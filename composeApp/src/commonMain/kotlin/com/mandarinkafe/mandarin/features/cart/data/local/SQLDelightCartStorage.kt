package com.mandarinkafe.mandarin.features.cart.data.local

import com.mandarinkafe.mandarin.shared.database.CartItemsQueries
import com.mandarinkafe.mandarin.features.cart.data.Mapper.toParams
import com.mandarinkafe.mandarin.features.cart.data.Mapper.toStoredCartItem
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem
import com.mandarinkafe.mandarin.util.getCurrentTimeMillis
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SQLDelightCartStorage(private val queries: CartItemsQueries) :
    CartStorage {
    override suspend fun getCartItems(): List<StoredCartItem> {
        return try {
            withContext(Dispatchers.Default) {
                queries.selectAll()
                    .executeAsList()
                    .map { it.toStoredCartItem() }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Default) {
                queries.deleteAll()
            }
            Napier.e("Ошибка при получении корзины из БД. Очищаю корзину. $e")
            emptyList()
        }
    }

    override suspend fun clearCart() {
        queries.deleteAll()
    }

    override suspend fun addOrUpdateItem(item: StoredCartItem) {
        val existingItem = queries.selectById(item.id).executeAsOneOrNull()
        val timestamp = existingItem?.timestamp ?: getCurrentTimeMillis()

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