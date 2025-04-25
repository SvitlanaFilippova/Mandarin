package com.mandarinkafe.mandarin.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UndoActionManager<T>(
    private val coroutineScope: CoroutineScope,
    private val delayMillis: Long = 10_000L
) {
    private val jobs = mutableMapOf<T, Job>()

    fun schedule(
        key: T,
        onTimeout: () -> Unit
    ) {
        jobs[key]?.cancel()
        jobs[key] = coroutineScope.launch {
            delay(delayMillis)
            onTimeout()
            jobs.remove(key)
        }
    }

    fun cancel(key: T) {
        jobs[key]?.cancel()
        jobs.remove(key)
    }

    fun cancelAll() {
        jobs.values.forEach { it.cancel() }
        jobs.clear()
    }

    fun isPending(key: T): Boolean = jobs.containsKey(key)
}