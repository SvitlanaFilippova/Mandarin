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
 */
fun NavBackStackEntry.getBooleanArgument(key: String, defaultValue: Boolean = false): Boolean {
    return savedStateHandle.get<Boolean>(key) ?: defaultValue
}



