package com.mandarinkafe.mandarin.navigation

import androidx.navigation.NavBackStackEntry

/**
 * Кроссплатформенная функция для получения строкового параметра из navigation arguments
 */
fun NavBackStackEntry.getStringArgument(key: String): String? {
    return savedStateHandle.get<String>(key)
}


/**
 * Кроссплатформенная функция для получения boolean параметра из navigation arguments
 *
 * На iOS Navigation Compose может сохранять boolean как Boolean, а не как String.
 * Поэтому сначала пытаемся получить как Boolean, затем как String.
 */
fun NavBackStackEntry.getBooleanArgument(key: String, defaultValue: Boolean = false): Boolean {
    if (!savedStateHandle.contains(key)) {
        return defaultValue
    }

    // На iOS Navigation Compose может сохранять boolean как Boolean, а не как String
    // Поэтому сначала пытаемся получить как Boolean
    val booleanResult = runCatching {
        @Suppress("UNCHECKED_CAST")
        savedStateHandle.get<Boolean>(key)
    }

    if (booleanResult.isSuccess) {
        val booleanValue = booleanResult.getOrNull()
        if (booleanValue != null) {
            return booleanValue
        }
    }

    // Если не Boolean, пытаемся получить как String (параметры из URL приходят как строки)
    val stringResult = runCatching {
        savedStateHandle.get<String>(key)
    }

    if (stringResult.isSuccess) {
        val stringValue = stringResult.getOrNull()

        // Защита: проверяем длину и валидность значения
        // "true"/"false" максимум 5 символов, но оставляем запас для безопасности
        if (stringValue != null && stringValue.length > 0 && stringValue.length <= MAX_BOOLEAN_STRING_LENGTH) {
            return when (stringValue.lowercase()) {
                "true" -> true
                "false" -> false
                else -> defaultValue
            }
        }
        return defaultValue
    }

    // Если не получилось получить ни как Boolean, ни как String, возвращаем defaultValue
    return defaultValue
}

private const val MAX_BOOLEAN_STRING_LENGTH = 10

