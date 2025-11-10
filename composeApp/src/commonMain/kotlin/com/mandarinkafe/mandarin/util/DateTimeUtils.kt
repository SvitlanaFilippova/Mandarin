package com.mandarinkafe.mandarin.util

import io.github.aakira.napier.Napier
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number

object DateTimeUtils {
    fun String?.toHumanDateTimeOrNull(): String? = try {
        this?.let {
            if (it.isBlank()) return null

            // Парсим формат "yyyy-MM-dd HH:mm:ss.SSS" или "yyyy-MM-dd HH:mm:ss"
            val dateString = if (it.contains('.')) {
                it.substringBefore('.')
            } else {
                it
            }

            // Заменяем пробел на 'T' для совместимости с ISO форматом
            val isoDateString = dateString.replace(" ", "T")
            val dateTime = LocalDateTime.parse(isoDateString)

            // Форматируем в "HH:mm, dd.MM.yyyy"
            val hour = dateTime.hour.toString().padStart(2, '0')
            val minute = dateTime.minute.toString().padStart(2, '0')
            val day = dateTime.day.toString().padStart(2, '0')
            val month = dateTime.month.number.toString().padStart(2, '0')
            val year = dateTime.year

            "$hour:$minute, $day.$month.$year"
        }
    } catch (e: Exception) {
        Napier.e("String toHumanDateTimeOrNull error: $e")
        null
    }

}
