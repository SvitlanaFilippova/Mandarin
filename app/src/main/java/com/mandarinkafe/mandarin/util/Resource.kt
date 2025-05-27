package com.mandarinkafe.mandarin.util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    /**
     * Исходное состояние, когда загрузка ещё не начналась
     */
    class Idle<T> : Resource<T>()

    /**
     * Загрузка в процессе
     */
    class Loading<T> : Resource<T>()

    /**
     * Получен успешный результат
     */
    class Success<T>(data: T) : Resource<T>(data)

    /**
     * Загрузка завершилась с ошибкой
     */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}