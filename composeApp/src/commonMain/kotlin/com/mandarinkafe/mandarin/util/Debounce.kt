package com.mandarinkafe.mandarin.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <T> debounce(
    delayMillis: Long,
    coroutineScope: CoroutineScope,
    cancelPrevious: Boolean = false, // 👈 новое
    action: (T) -> Unit,
): DebounceResult<T> {
    var lastJob: Job? = null
    val debounceJobs = mutableMapOf<T, Job>()

    val cancel: () -> Unit = {
        lastJob?.cancel()
        debounceJobs.values.forEach { it.cancel() }
        debounceJobs.clear()
    }

    val invoke: (T) -> Unit = { param: T ->
        if (cancelPrevious) {
            lastJob?.cancel()
        }

        val job = coroutineScope.launch {
            delay(delayMillis)
            action(param)
        }

        job.invokeOnCompletion {
            if (lastJob == job) lastJob = null
            debounceJobs.remove(param)
        }

        lastJob = job
        debounceJobs[param] = job
    }

    return DebounceResult(cancel, invoke)
}