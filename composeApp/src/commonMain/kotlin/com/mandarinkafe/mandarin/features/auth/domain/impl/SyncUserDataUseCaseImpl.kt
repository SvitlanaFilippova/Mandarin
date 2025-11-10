package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.FavoritesWriter
import com.mandarinkafe.mandarin.features.auth.domain.api.SyncUserDataUseCase
import com.mandarinkafe.mandarin.features.cart.data.impl.CartRepositoryImpl

class SyncUserDataUseCaseImpl(
    private val authStateChecker: AuthStateChecker,
    private val favoritesRepository: FavoritesWriter,
    private val cartRepository: CartRepositoryImpl,
) : SyncUserDataUseCase {
    override suspend fun invoke() {
        // Синхронизируем данные только если пользователь авторизован
        if (!authStateChecker.isAuthorizedFast()) {
            return
        }
        favoritesRepository.sync()
        cartRepository.sync()
    }
}