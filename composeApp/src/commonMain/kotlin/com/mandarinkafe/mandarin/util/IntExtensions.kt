package com.mandarinkafe.mandarin.util

/**
 * Форматирует количество секунд в формат MM:SS
 * @return строка в формате MM:SS (например, "05:00", "04:59", "00:30")
 */
fun Int.toTimeFormat(): String {
    val minutes = this / 60
    val seconds = this % 60
    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}




