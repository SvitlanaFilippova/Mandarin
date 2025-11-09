package com.mandarinkafe.mandarin.features.cart.data.remote

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.cart.data.Mapper.toDto
import com.mandarinkafe.mandarin.features.cart.data.Mapper.toStored
import com.mandarinkafe.mandarin.features.cart.data.models.CartMetadata
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem
import com.mandarinkafe.mandarin.features.cart.data.network.CartServerApi
import io.github.aakira.napier.Napier

class CartRemoteDataSourceImpl(
    private val api: CartServerApi,
    private val authRepository: AuthRepository,
    private val menuCache: MenuCache,
) : CartRemoteDataSource {

    override suspend fun getCart(): RemoteCart {
        val token = authRepository.getAccessToken()
        if (token == null) {
            Napier.w("[CartSync] getCart: токен отсутствует")
            return RemoteCart(emptyList(), CartMetadata())
        }
        return try {
            Napier.d("[CartSync] getCart: запрос корзины с сервера")
            val response = api.getCart("Bearer $token")
            val items = response.items.mapNotNull { cartItemDto ->
                val stored = cartItemDto.toStored(menuCache)
                if (stored != null) {
                    Napier.d("[CartSync] getCart: получен элемент id=${stored.id}, mealId=${stored.mealId}, quantity=${stored.quantity}, timestamp=${stored.timestamp}")
                } else {
                    Napier.w("[CartSync] getCart: не удалось преобразовать DTO в StoredCartItem, mealId=${cartItemDto.mealId}")
                }
                stored
            }
            val metadata = CartMetadata(
                updatedAt = response.updatedAt,
                isDeleted = response.isDeleted
            )
            Napier.d("[CartSync] getCart: получено элементов с сервера: ${items.size}, updatedAt=${metadata.updatedAt}, isDeleted=${metadata.isDeleted}")
            RemoteCart(items, metadata)
        } catch (e: Exception) {
            Napier.e("[CartSync] getCart: ошибка при получении корзины с сервера", e)
            RemoteCart(emptyList(), CartMetadata())
        }
    }

    override suspend fun syncCart(localCart: List<StoredCartItem>, metadata: CartMetadata) {
        val token = authRepository.getAccessToken()
        if (token == null) {
            Napier.w("[CartSync] syncCart: токен отсутствует")
            return
        }
        try {
            Napier.d("[CartSync] syncCart: отправка корзины на сервер, элементов=${localCart.size}, updatedAt=${metadata.updatedAt}, isDeleted=${metadata.isDeleted}")
            localCart.forEach { item ->
                Napier.d("[CartSync] syncCart: отправка элемента id=${item.id}, mealId=${item.mealId}, quantity=${item.quantity}, timestamp=${item.timestamp}")
            }
            val cartItemDtos = localCart.map { it.toDto() }
            Napier.d("[CartSync] syncCart: преобразовано в DTO: ${cartItemDtos.size} элементов")
            api.updateCart("Bearer $token", cartItemDtos)
            Napier.d("[CartSync] syncCart: корзина успешно отправлена на сервер")
        } catch (e: Exception) {
            Napier.e("[CartSync] syncCart: ошибка при отправке корзины на сервер", e)
        }
    }

    override suspend fun clearCart() {
        val token = authRepository.getAccessToken()
        if (token == null) {
            Napier.w("[CartSync] clearCart: токен отсутствует")
            return
        }
        try {
            Napier.d("[CartSync] clearCart: отправка DELETE /cart на сервер")
            api.clearCart("Bearer $token")
            Napier.d("[CartSync] clearCart: корзина успешно очищена на сервере")
        } catch (e: Exception) {
            Napier.e("[CartSync] clearCart: ошибка при очистке корзины на сервере", e)
        }
    }
}

