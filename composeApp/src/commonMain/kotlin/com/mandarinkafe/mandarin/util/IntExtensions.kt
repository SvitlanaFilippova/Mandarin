package com.mandarinkafe.mandarin.util

private const val SECONDS_IN_MINUTE = 60
private const val MIN_DIGITS_FOR_TIME_FORMAT = 2

/**
 * Форматирует количество секунд в формат MM:SS
 * @return строка в формате MM:SS (например, "05:00", "04:59", "00:30")
 */
fun Int.toTimeFormat(): String {
    val minutes = this / SECONDS_IN_MINUTE
    val seconds = this % SECONDS_IN_MINUTE
    return "${minutes.toString().padStart(MIN_DIGITS_FOR_TIME_FORMAT, '0')}:${
        seconds.toString().padStart(MIN_DIGITS_FOR_TIME_FORMAT, '0')
    }"
}




