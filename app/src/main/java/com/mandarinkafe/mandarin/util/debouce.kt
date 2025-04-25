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
    var debounceJob: Job? = null

    val cancel: () -> Unit = {
        debounceJob?.cancel()
        debounceJob = null
    }

    val invoke: (T) -> Unit = { param: T ->
        if (useLastParam) {
            debounceJob?.cancel()
        }
        if (debounceJob?.isCompleted != false || useLastParam) {
            debounceJob = coroutineScope.launch {
                delay(delayMillis)
                action(param)
            }
        }
    }

    return DebounceResult(cancel, invoke)
}