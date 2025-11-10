package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.CartReader
import com.mandarinkafe.mandarin.core.domain.api.FavoritesWriter
import com.mandarinkafe.mandarin.features.auth.domain.api.SyncUserDataUseCase
import com.mandarinkafe.mandarin.features.cart.data.impl.CartRepositoryImpl
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.first

class SyncUserDataUseCaseImpl(
    private val authStateChecker: AuthStateChecker,
    private val favoritesRepository: FavoritesWriter,
    private val cartRepository: CartRepositoryImpl,
    private val cartReader: CartReader,
) : SyncUserDataUseCase {
    override suspend fun invoke(): Boolean {
        // Синхронизируем данные только если пользователь авторизован
        if (!authStateChecker.isAuthorizedFast()) {
            return false
        }

        // Сохраняем состояние корзины до синхронизации
        val cartBefore = cartReader.observeCartItems().first()
        val cartItemsBefore = when (cartBefore) {
            is Resource.Success -> cartBefore.data ?: emptyList()
            else -> emptyList()
        }

        favoritesRepository.sync()
        cartRepository.sync()

        // Ждем обновления корзины после синхронизации
        // Берем первое значение, которое не Loading и не Idle
        val cartAfter = cartReader.observeCartItems()
            .first { it !is Resource.Loading && it !is Resource.Idle }
        val cartItemsAfter = when (cartAfter) {
            is Resource.Success -> cartAfter.data ?: emptyList()
            else -> emptyList()
        }

        // Сравниваем корзины по содержимому (id, quantity, customizedMeal)
        val cartChanged = cartItemsBefore != cartItemsAfter

        return cartChanged
    }
}