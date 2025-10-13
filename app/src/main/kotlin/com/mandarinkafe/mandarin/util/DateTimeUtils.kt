package com.mandarinkafe.mandarin.util

import io.github.aakira.napier.Napier
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeUtils {
    fun String?.toHumanDateTimeOrNull(): String? = try {
        this?.let {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            val outputFormatter = DateTimeFormatter.ofPattern("HH:mm, dd.MM.yyyy", Locale("ru"))
            LocalDateTime.parse(it, inputFormatter).format(outputFormatter)
        }
    } catch (e: Exception) {
        Napier.e("error: $e")
        null
    }
}