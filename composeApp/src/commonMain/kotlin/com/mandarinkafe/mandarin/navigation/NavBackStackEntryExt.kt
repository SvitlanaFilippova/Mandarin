package com.mandarinkafe.mandarin.navigation

import androidx.navigation.NavBackStackEntry

/**
 * Кроссплатформенная функция для получения строкового параметра из navigation arguments
 */
fun NavBackStackEntry.getStringArgument(key: String): String? {
    return savedStateHandle.get<String>(key)
}

/**
 * Кроссплатформенная функция для получения строкового параметра с дефолтным значением
 */
fun NavBackStackEntry.getStringArgument(key: String, defaultValue: String): String {
    return savedStateHandle.get<String>(key) ?: defaultValue
}

/**
 * Кроссплатформенная функция для получения boolean параметра из navigation arguments
 */
fun NavBackStackEntry.getBooleanArgument(key: String, defaultValue: Boolean = false): Boolean {
    return savedStateHandle.get<String>(key)?.toBoolean() ?: defaultValue
}

/**
 * Кроссплатформенная функция для получения double параметра из navigation arguments
 */
fun NavBackStackEntry.getDoubleArgument(key: String, defaultValue: Double = 0.0): Double {
    return savedStateHandle.get<String>(key)?.toDoubleOrNull() ?: defaultValue
}

