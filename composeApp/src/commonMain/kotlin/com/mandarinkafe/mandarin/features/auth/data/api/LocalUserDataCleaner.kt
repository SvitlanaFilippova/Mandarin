package com.mandarinkafe.mandarin.features.auth.data.api

/**
 * Интерфейс для очистки локальных данных пользователя (корзина, избранное).
 * Используется при выходе из системы или при невалидных токенах.
 */
interface LocalUserDataCleaner {
    suspend fun clear()
}

