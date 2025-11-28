package com.mandarinkafe.mandarin.util

import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
actual fun getCurrentTimeMillis(): Long = kotlin.time.Clock.System.now().toEpochMilliseconds()
