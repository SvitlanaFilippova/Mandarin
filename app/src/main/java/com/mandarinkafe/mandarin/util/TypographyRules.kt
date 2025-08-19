@file:Suppress("MagicNumber")

package com.mandarinkafe.mandarin.util

import com.mandarinkafe.mandarin.util.Constants.NON_BRAKING_SPACE

object TypographyRules {
    /**
     * Список коротких слов (предлогов, союзов и частиц), перед которыми нужен неразрывный пробел
     */
    val shortWords = listOf(
        "и", "в", "во", "не", "на", "за", "из", "от",
        "по", "о", "об", "а", "с", "со", "у", "к", "до", "без", "для"
    )
}

/**
 * Функция для применения правил типографики к строке
 */
fun String.applyTypography(): String {
    val shortWordRegex = Regex(
        """\b(${TypographyRules.shortWords.joinToString("|")})\s""",
        RegexOption.IGNORE_CASE
    )

    return this
        // Удаление лишних пробелов (2 и более)
        .replace(Regex("""\s{2,}"""), " ")
        // Исправляем дефис без пробела после переноса
        .replace(Regex("""(?<=^|\s)-(?=\S)""")) { "- " }
        // Неразрывный пробел после коротких слов
        .replace(shortWordRegex) { matchResult ->
            matchResult.groupValues[1] + NON_BRAKING_SPACE
        }
        // Неразрывные числа (десятичные дроби с разрядами типа 12 500)
        .normalizeNumbers()
        // Нормализация граммов и сантиметров
        .normalizeUnits()
        // Многоточие
        .replace("...", "…")
        // Тире (если дефис окружён пробелами)
        .replace(Regex("""(?<=\s)-(?=\s)"""), "—")
        // Кавычки-ёлочки
        .replace("« ", "«")
        .replace(" »", "»")

        // Удаление пробелов перед запятыми
        .replace(Regex("""\s+,"""), ",")
        // Пробел после запятой (если пропущен), кроме случаев с числами (0,5)
        .replace(Regex(""",(?=\S)(?<!\d,)(?!\d)"""), ", ")
        // Удаление пробелов в начале и конце строки
        .trim()
        // Удаление запятой в конце строки
        .replace(Regex(""",\s*$"""), "")
        // Первая заглавная буква
        .replaceFirstChar { it.uppercaseChar() }
}

/**
 * Нормализация единиц измерения (граммы, сантиметры и т.п.)
 */
private fun String.normalizeUnits(): String {
    return this
        // "гр" → "г"
        .replace(Regex("""\b(\d+)\s*(гр|ГР|Гр|гР)\.?\b""")) {
            "${it.groupValues[1]}${NON_BRAKING_SPACE}г"
        }
        // "г" после числа → неразрывный пробел
        .replace(Regex("""(\d+)\s*(г|Г)\.?""")) {
            "${it.groupValues[1]}$NON_BRAKING_SPACE${it.groupValues[2].lowercase()}"
        }
        // сантиметры
        .replace(Regex("""(\d+)\s*(см|См)(\.)?""")) {
            "${it.groupValues[1]}$NON_BRAKING_SPACE${it.groupValues[2].lowercase()}${it.groupValues[3]}"
        }
}

/**
 * Нормализация чисел: десятичные дроби и числа с разрядами через неразрывный пробел
 */
private fun String.normalizeNumbers(): String {
    return this
        // 0, 5 → 0,5 (убираем лишний пробел)
        .replace(Regex("""(\d),\s+(\d)""")) { "${it.groupValues[1]},${it.groupValues[2]}" }
        // Дроби (оставляем просто запятую)
        .replace(Regex("""(\d),(\d)""")) { "${it.groupValues[1]},${it.groupValues[2]}" }
        // Большие числа (группы по 3 разряда, универсально)
        .replace(Regex("""\b(?=(\d{4,}))(?:\d{1,3})(?=(\d{3})+(?!\d))""")) {
            it.value + NON_BRAKING_SPACE
        }
}

/**
 * Для удаления "-" в начале названия модификаторов/добавок
 */
fun String.removeLeadingDash(): String {
    return this.replaceFirst(Regex("""^\s*-\s*"""), "")
}