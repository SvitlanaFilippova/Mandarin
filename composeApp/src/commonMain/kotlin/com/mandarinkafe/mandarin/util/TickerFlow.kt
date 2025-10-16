package com.mandarinkafe.mandarin.util

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration

fun tickerFlow(
    period: Duration,
    initialDelay: Duration = period
): Flow<Unit> = flow {
    delay(initialDelay)
    val context = currentCoroutineContext()
    while (context.isActive) {
        emit(Unit)
        delay(period)
    }
}

