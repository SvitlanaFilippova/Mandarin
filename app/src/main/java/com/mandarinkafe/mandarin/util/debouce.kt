package com.mandarinkafe.mandarin.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <T> debounce(
    delayMillis: Long,
    coroutineScope: CoroutineScope,
    useLastParam: Boolean,
    action: (T) -> Unit
): DebounceResult<T> {
    // Коллекция для хранения активных задач
    val debounceJobs = mutableMapOf<T, Job>()

    val cancel: () -> Unit = {
        debounceJobs.values.forEach { it.cancel() }
        debounceJobs.clear()
    }

    val invoke: (T) -> Unit = { param: T ->
        if (useLastParam) {
            debounceJobs[param]?.cancel()
        }

        val job = coroutineScope.launch {
            delay(delayMillis)
            action(param)
        }

        // Сохраняем новую задачу в коллекции
        debounceJobs[param] = job
    }

    return DebounceResult(cancel, invoke)
}