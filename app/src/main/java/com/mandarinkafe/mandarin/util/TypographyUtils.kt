package com.mandarinkafe.mandarin.util

object TypographyRules {
    /**
     * Список коротких слов (предлогов, союзов и частиц), перед которыми нужен неразрывный пробел
     */
    val shortWords = listOf(
        "и", "в", "во", "не", "на", "за", "из", "от", "см", "г",
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
        // 0. Нормализация граммов
        .normalizeWeight()
        // 1. Исправляем дефис без пробела после переноса
        .replace(Regex("""(?<=^|\s)-(?=\S)""")) { "- " }
        // 2. Неразрывный пробел после коротких слов
        .replace(shortWordRegex) { matchResult ->
            matchResult.groupValues[1] + nonBreakingSpace
        }
        // 3. Неразрывные числа (десятичные дроби типа 0,33 и числа с разрядами типа 12 500)
        .normalizeNumbers()
        // 4. Многоточие
        .replace("...", "…")
        // 5. Тире (если дефис окружён пробелами)
        .replace(Regex("""(?<=\s)-(?=\s)"""), "—")
        // 6. Кавычки-ёлочки
        .replace("« ", "«")
        .replace(" »", "»")
        // 7. Удаление лишних пробелов (2 и более)
        .replace(Regex("""\s{2,}"""), " ")
        // 8. Удаление пробелов перед запятыми
        .replace(Regex("""\s+,"""), ",")
        // 9. Пробел после запятой (если пропущен)
        .replace(Regex(""",(?=\S)"""), ", ")
        // 10. Удаление пробелов в начале и конце строки
        .trim()
}

/**
 * Нормализация записи веса (граммов) в строках
 */
private fun String.normalizeWeight(): String {
    return this.replace(Regex("""(\d+)\s*(г|Г)\.?""")) { matchResult ->
        val number = matchResult.groupValues[1]
        "$number г"
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
    return this.removePrefix("-").trimStart()
}