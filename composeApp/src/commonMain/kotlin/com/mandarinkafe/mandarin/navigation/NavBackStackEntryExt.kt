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
 * Параметры из URL приходят как строки, поэтому безопасно получаем значение
 * и преобразуем его в Boolean
 */
fun NavBackStackEntry.getBooleanArgument(key: String, defaultValue: Boolean = false): Boolean {
    if (!savedStateHandle.contains(key)) {
        return defaultValue
    }

    // Пытаемся получить как String (параметры из URL всегда строки)
    val stringResult = runCatching {
        savedStateHandle.get<String>(key)
    }

    if (stringResult.isSuccess) {
        val stringValue = stringResult.getOrNull()

        // Защита: проверяем длину и валидность значения
        // "true"/"false" максимум 5 символов, но оставляем запас для безопасности
        if (stringValue != null && stringValue.length <= MAX_BOOLEAN_STRING_LENGTH) {
            return runCatching {
                when (stringValue.lowercase()) {
                    "true" -> true
                    "false" -> false
                    else -> defaultValue
                }
            }.getOrDefault(defaultValue)
        }
        // Если строка слишком длинная или null, возвращаем defaultValue
        return defaultValue
    }

    // Если не String, пытаемся получить как Boolean (если было установлено программно)
    val booleanResult = runCatching {
        @Suppress("UNCHECKED_CAST")
        savedStateHandle.get<Boolean>(key)
    }

    return booleanResult.getOrNull() ?: defaultValue
}

private const val MAX_BOOLEAN_STRING_LENGTH = 10

