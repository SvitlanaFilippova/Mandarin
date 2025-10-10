package com.mandarinkafe.mandarin.util

import kotlinx.datetime.Clock

actual fun getCurrentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()
