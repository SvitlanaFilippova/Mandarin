package com.mandarinkafe.mandarin.util

data class DebounceResult<T>(
    val cancel: () -> Unit,
    val invoke: (T) -> Unit
)