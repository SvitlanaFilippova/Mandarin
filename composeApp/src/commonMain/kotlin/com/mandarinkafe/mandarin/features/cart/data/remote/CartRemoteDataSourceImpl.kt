package com.mandarinkafe.mandarin.features.cart.data.remote

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.cart.data.Mapper.toDto
import com.mandarinkafe.mandarin.features.cart.data.Mapper.toStored
import com.mandarinkafe.mandarin.features.cart.data.models.Cart
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem
import com.mandarinkafe.mandarin.features.cart.data.network.CartServerApi
import io.github.aakira.napier.Napier

class CartRemoteDataSourceImpl(
    private val api: CartServerApi,
    private val authRepository: AuthRepository,
    private val menuCache: MenuCache,
) : CartRemoteDataSource {

    override suspend fun getCart(): Cart {
        val token = authRepository.getAccessToken()
        if (token == null) {
            Napier.w("[CartSync] getCart: токен отсутствует")
            return Cart(emptyList(), 0L)
        }
        return try {
            Napier.d("[CartSync] getCart: запрос корзины с сервера")
            val response = api.getCart("Bearer $token")
            val items = response.items.mapNotNull { cartItemDto ->
                val stored = cartItemDto.toStored(menuCache)
                if (stored != null) {
                    Napier.d("[CartSync] getCart: получен элемент id=${stored.id}, mealId=${stored.mealId}, quantity=${stored.quantity}, createdAt=${stored.createdAt}, updatedAt=${stored.updatedAt}")
                } else {
                    Napier.w("[CartSync] getCart: не удалось преобразовать DTO в StoredCartItem, mealId=${cartItemDto.mealId}")
                }
                stored
            }
            Napier.d("[CartSync] getCart: получено элементов с сервера: ${items.size}, lastUpdated=${response.lastUpdated}")
            Cart(items, response.lastUpdated)
        } catch (e: Exception) {
            Napier.e("[CartSync] getCart: ошибка при получении корзины с сервера", e)
            Cart(emptyList(), 0L)
        }
    }

    override suspend fun syncCart(localCart: List<StoredCartItem>): Cart {
        val token = authRepository.getAccessToken()
        if (token == null) {
            Napier.w("[CartSync] syncCart: токен отсутствует")
            return Cart(emptyList(), 0L)
        }
        return try {
            Napier.d("[CartSync] syncCart: отправка корзины на сервер, элементов=${localCart.size}")
            localCart.forEach { item ->
                Napier.d("[CartSync] syncCart: отправка элемента id=${item.id}, mealId=${item.mealId}, quantity=${item.quantity}, createdAt=${item.createdAt}, updatedAt=${item.updatedAt}")
                if (item.updatedAt == 0L) {
                    Napier.d("[CartSync] syncCart: элемент id=${item.id} помечен как измененный (updatedAt=0)")
                } else {
                    Napier.d("[CartSync] syncCart: элемент id=${item.id} не изменен, отправляем updatedAt=${item.updatedAt} для мержа на сервере")
                }
            }
            // Отправляем корзину с реальными updatedAt:
            // - updatedAt = 0L → маркер "эта позиция изменена/новая, обновляй"
            // - updatedAt > 0 → реальное значение для мержа на сервере
            val cartItemDtos = localCart.map { item ->
                item.toDto() // Отправляем как есть, включая реальные updatedAt
            }
            Napier.d("[CartSync] syncCart: преобразовано в DTO: ${cartItemDtos.size} элементов")
            val response = api.updateCart("Bearer $token", cartItemDtos)
            // Получаем обновлённую корзину с updatedAt от сервера
            val items = response.items.mapNotNull { cartItemDto ->
                val stored = cartItemDto.toStored(menuCache)
                if (stored != null) {
                    Napier.d("[CartSync] syncCart: получен обновленный элемент id=${stored.id}, updatedAt=${stored.updatedAt}")
                }
                stored
            }
            Napier.d("[CartSync] syncCart: получена обновленная корзина с сервера, элементов=${items.size}, lastUpdated=${response.lastUpdated}")
            Cart(items, response.lastUpdated)
        } catch (e: Exception) {
            Napier.e("[CartSync] syncCart: ошибка при отправке корзины на сервер", e)
            Cart(emptyList(), 0L)
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

