package com.mandarinkafe.mandarin.util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
) {
    /**
     * Исходное состояние, когда загрузка ещё не начиналась
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
     * Получен успешный результат, но данные пустые
     */
    class ErrorEmptyData<T> : Resource<T>()

    /**
     * Отсутствует соединение
     */
    class ErrorNoInternet<T> : Resource<T>()

    /**
     * Другие ошибки
     */
    class ErrorOther<T>(message: String) : Resource<T>(message = message)
}