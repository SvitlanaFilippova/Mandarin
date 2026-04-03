package com.mandarinkafe.mandarin.features.menu.domain.models

import com.mandarinkafe.mandarin.util.Constants

/** Для строк с «работает до %s», если время не пришло — em dash. Не использовать для `isClosedForWholeDay`. */
fun OrderAcceptStatusSnapshot.closingTimeOrPlaceholder(): String =
    closingTime?.trim()?.takeIf { it.isNotBlank() }
        ?: Constants.CLOSING_TIME_PLACEHOLDER_EM_DASH
