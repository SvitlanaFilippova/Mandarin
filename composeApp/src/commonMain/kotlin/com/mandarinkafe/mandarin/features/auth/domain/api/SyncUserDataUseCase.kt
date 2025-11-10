package com.mandarinkafe.mandarin.features.auth.domain.api

interface SyncUserDataUseCase {
    /**
     * Синхронизирует данные пользователя (корзина, избранное) с сервером.
     * @return true, если корзина изменилась после синхронизации
     */
    suspend operator fun invoke(): Boolean
}