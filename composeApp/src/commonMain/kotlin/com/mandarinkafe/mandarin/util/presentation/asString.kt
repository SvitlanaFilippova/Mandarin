package com.mandarinkafe.mandarin.util.presentation

// TODO: Реализовать правильное преобразование в строку для KMP
fun Any.asString(): String {
    return when (this) {
        is String -> this
        is Int -> this.toString()
        is Long -> this.toString()
        is Double -> this.toString()
        is Float -> this.toString()
        else -> this.toString()
    }
}
