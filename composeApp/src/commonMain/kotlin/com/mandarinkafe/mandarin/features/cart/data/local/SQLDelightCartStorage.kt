package com.mandarinkafe.mandarin.features.cart.data.local

import com.mandarinkafe.mandarin.database.CartItemsQueries
import com.mandarinkafe.mandarin.features.cart.data.Mapper.toParams
import com.mandarinkafe.mandarin.features.cart.data.Mapper.toStoredCartItem
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem
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
        val params = item.toParams()
        with(params) {
            queries.insertOrReplace(
                id = id,
                mealId = mealId,
                addsIds = addsJson,
                modifiers = modifiersJson,
                quantity = quantity,
                comment = comment,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }

    override suspend fun deleteItemById(id: String) {
        queries.deleteById(id)
    }

    override suspend fun getLastUpdated(): Long {
        return try {
            withContext(Dispatchers.Default) {
                val metadataRow = queries.selectMetadata().executeAsOneOrNull()
                metadataRow?.lastUpdated ?: 0L
            }
        } catch (e: Exception) {
            Napier.e("Ошибка при получении lastUpdated из БД: $e")
            0L
        }
    }

    override suspend fun updateLastUpdated(lastUpdated: Long) {
        withContext(Dispatchers.Default) {
            queries.insertOrReplaceMetadata(lastUpdated = lastUpdated)
        }
    }
}