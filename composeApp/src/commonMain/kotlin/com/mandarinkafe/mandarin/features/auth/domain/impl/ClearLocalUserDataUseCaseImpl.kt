package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.features.auth.domain.api.ClearLocalUserDataUseCase
import com.mandarinkafe.mandarin.features.cart.data.local.CartStorage
import com.mandarinkafe.mandarin.features.favorites.data.datastore.FavoritesStorage
import io.github.aakira.napier.Napier

class ClearLocalUserDataUseCaseImpl(
    private val cartStorage: CartStorage,
    private val favoritesStorage: FavoritesStorage,
) : ClearLocalUserDataUseCase {
    override suspend fun invoke() {
        // Очищаем локальные данные корзины (без отправки на сервер)
        try {
            cartStorage.clearCart()
            cartStorage.updateLastUpdated(0L)
        } catch (e: Exception) {
            Napier.e("Ошибка при очистке локальной корзины при логауте", e)
        }

        // Очищаем локальные данные избранного (без отправки на сервер)
        try {
            favoritesStorage.saveFavorites(emptySet())
            favoritesStorage.updateLastUpdated(0L)
        } catch (e: Exception) {
            Napier.e("Ошибка при очистке локального избранного при логауте", e)
        }
    }
}

