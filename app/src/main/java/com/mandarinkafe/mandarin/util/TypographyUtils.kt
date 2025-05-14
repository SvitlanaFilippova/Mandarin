package com.mandarinkafe.mandarin.util

object TypographyRules {
    /**
     * Список коротких слов (предлогов, союзов и частиц), перед которыми нужен неразрывный пробел
     */
    val shortWords = listOf(
        "и", "в", "во", "не", "на", "за", "из", "от",
        "по", "о", "об", "а", "с", "со", "у", "к", "до", "без", "для"
    )
}

fun String.applyTypography(): String {

    val nonBreakingSpace = "\u00A0"

    val shortWordRegex = Regex(
        """\b(${TypographyRules.shortWords.joinToString("|")})\s""",
        RegexOption.IGNORE_CASE
    )

    return this
        // Нормализация граммов и сантиметров
        .normalizeWeightAndUnits()
        // Исправляем дефис без пробела после переноса
        .replace(Regex("""(?<=^|\s)-(?=\S)""")) { "- " }
        // Неразрывный пробел после коротких слов
        .replace(shortWordRegex) { matchResult ->
            matchResult.groupValues[1] + nonBreakingSpace
        }
        // Неразрывные числа (десятичные дроби типа 0,33 и числа с разрядами типа 12 500)
        .normalizeNumbers()
        // Многоточие
        .replace("...", "…")
        // Тире (если дефис окружён пробелами)
        .replace(Regex("""(?<=\s)-(?=\s)"""), "—")
        // Кавычки-ёлочки
        .replace("« ", "«")
        .replace(" »", "»")
        // Удаление лишних пробелов (2 и более)
        .replace(Regex("""\s{2,}"""), " ")
        // Удаление пробелов перед запятыми
        .replace(Regex("""\s+,"""), ",")
        // Пробел после запятой (если пропущен)
        .replace(Regex(""",(?=\S)"""), ", ")
        // Удаление пробелов в начале и конце строки
        .trim()
        // Первая заглавная буква
        .replaceFirstChar { it.uppercaseChar() }
}

/**
 * Нормализация записи веса (граммов) и размера (в см) в строках
 */
private fun String.normalizeWeightAndUnits(): String {
    val nonBreakingSpace = "\u00A0"
    return this
        // "гр" после числа -> "г" с неразрывным пробелом
        .replace(Regex("""\b(\d+)\s*(гр|ГР|Гр|гР)\.?\b""")) {
            "${it.groupValues[1]}$nonBreakingSpace${"г"}"
        }
        // "г" после числа -> "г" с неразрывным пробелом
        .replace(Regex("""(\d+)\s*(г|Г)\.?""")) {
            "${it.groupValues[1]}$nonBreakingSpace${it.groupValues[2].lowercase()}"
        }
        // сантиметры (с сохранением точки, если она есть)
        .replace(Regex("""(\d+)\s*(см|См)(\.)?""")) {
            "${it.groupValues[1]}$nonBreakingSpace${it.groupValues[2].lowercase()}${it.groupValues[3]}"
        }
}

/**
 * Нормализация чисел: десятичные дроби и числа с разрядами через неразрывный пробел
 */
private fun String.normalizeNumbers(): String {
    val nonBreakingSpace = "\u00A0"

    return this
        // 1. Неразрывные дроби (0,33)
        .replace(Regex("""(\d),(\d)""")) { matchResult ->
            "${matchResult.groupValues[1]},${matchResult.groupValues[2]}"
                .replace(",", ",\u202F")
        }
        // 2. Неразрывные большие числа (12 500)
        .replace(Regex("""(\d{1,3}) (\d{3})(?!\d)""")) { matchResult ->
            val part1 = matchResult.groupValues[1]
            val part2 = matchResult.groupValues[2]
            "$part1$nonBreakingSpace$part2"
        }
}

fun String.removeLeadingDash(): String {
    return this.replaceFirst(Regex("""^\s*-\s*"""), "")
}