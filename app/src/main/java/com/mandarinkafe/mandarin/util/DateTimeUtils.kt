package com.mandarinkafe.mandarin.util

import android.util.Log
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
        Log.d("Error toHumanDateTimeOrNull", "error: $e")
        null
    }
}