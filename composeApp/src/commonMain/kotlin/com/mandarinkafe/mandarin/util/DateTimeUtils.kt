package com.mandarinkafe.mandarin.util

import io.github.aakira.napier.Napier
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.char

object DateTimeUtils {
    fun String?.toHumanDateTimeOrNull(): String? = try {
        this?.let {
            // Парсим формат "yyyy-MM-dd HH:mm:ss.SSS"
            val dateTime = LocalDateTime.parse(it.substringBefore('.'))
            
            // Форматируем в "HH:mm, dd.MM.yyyy"
            val hour = dateTime.hour.toString().padStart(2, '0')
            val minute = dateTime.minute.toString().padStart(2, '0')
            val day = dateTime.dayOfMonth.toString().padStart(2, '0')
            val month = dateTime.monthNumber.toString().padStart(2, '0')
            val year = dateTime.year
            
            "$hour:$minute, $day.$month.$year"
        }
    } catch (e: Exception) {
        Napier.e("error: $e")
        null
    }
}

