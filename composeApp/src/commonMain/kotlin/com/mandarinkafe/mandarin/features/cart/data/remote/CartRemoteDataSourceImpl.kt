package com.mandarinkafe.mandarin.features.cart.data.remote

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.cart.data.Mapper.toDto
import com.mandarinkafe.mandarin.features.cart.data.Mapper.toStored
import com.mandarinkafe.mandarin.features.cart.data.models.CartMetadata
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem
import com.mandarinkafe.mandarin.features.cart.data.network.CartServerApi
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import io.github.aakira.napier.Napier

class CartRemoteDataSourceImpl(
    private val api: CartServerApi,
    private val authRepository: AuthRepository,
    private val menuCache: MenuCache,
) : CartRemoteDataSource {

    override suspend fun getCart(): CartMetadata {
        val token = authRepository.getAccessToken()
        if (token == null) {
            return CartMetadata(emptyList(), 0L)
        }
        return try {
            val response = api.getCart("Bearer $token")
            val items = response.items.mapNotNull { cartItemDto ->
                cartItemDto.toStored(menuCache)
            }
            CartMetadata(items, response.lastUpdated)
        } catch (e: Exception) {
            Napier.e("Ошибка при получении корзины с сервера", e)
            CartMetadata(emptyList(), 0L)
        }
    }

    override suspend fun syncCart(localCart: List<StoredCartItem>): CartMetadata {
        val token = authRepository.getAccessToken()
        if (token == null) {
            return CartMetadata(emptyList(), 0L)
        }
        return try {
            // Отправляем корзину с реальными updatedAt:
            // - updatedAt = 0L → маркер "эта позиция изменена/новая, обновляй"
            // - updatedAt > 0 → реальное значение для мержа на сервере
            val cartItemDtos = localCart.map { item ->
                item.toDto() // Отправляем как есть, включая реальные updatedAt
            }
            val response = api.updateCart("Bearer $token", cartItemDtos)
            
            // Проверяем, была ли ошибка на сервере
            if (response.resultCode != HTTP_SUCCESS) {
                // При ошибке возвращаем локальную корзину, чтобы не потерять данные
                return CartMetadata(localCart, 0L)
            }
            
            // Получаем обновлённую корзину с updatedAt от сервера
            val items = response.items.mapNotNull { cartItemDto ->
                cartItemDto.toStored(menuCache)
            }
            CartMetadata(items, response.lastUpdated)
        } catch (e: Exception) {
            Napier.e("Ошибка при отправке корзины на сервер", e)
            CartMetadata(emptyList(), 0L)
        }
    }

    override suspend fun clearCart() {
        val token = authRepository.getAccessToken()
        if (token == null) {
            return
        }
        try {
            api.clearCart("Bearer $token")
        } catch (e: Exception) {
            Napier.e("Ошибка при очистке корзины на сервере", e)
        }
    }
}

