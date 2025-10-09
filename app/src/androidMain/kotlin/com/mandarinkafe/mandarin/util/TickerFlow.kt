package com.mandarinkafe.mandarin.util

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import kotlin.time.Duration

@OptIn(ObsoleteCoroutinesApi::class)
fun tickerFlow(
    period: Duration,
    initialDelay: Duration = period
): Flow<Unit> = channelFlow {
    val tickerChannel = ticker(
        delayMillis = period.inWholeMilliseconds,
        initialDelayMillis = initialDelay.inWholeMilliseconds
    )

    for (event in tickerChannel) {
        if (!isActive) break
        send(Unit)
    }
}