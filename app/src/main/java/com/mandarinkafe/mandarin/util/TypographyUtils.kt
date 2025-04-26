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
        // 3. Многоточие
        .replace("...", "…")
        // 4. Тире (только если дефис окружён пробелами)
        .replace(Regex("""(?<=\s)-(?=\s)"""), "—")
        // 5. Кавычки-ёлочки
        .replace("« ", "«")
        .replace(" »", "»")
        // 6. Удаление лишних пробелов (2 и более)
        .replace(Regex("""\s{2,}"""), " ")
        // 7. Удаление пробелов перед запятыми
        .replace(Regex("""\s+,"""), ",")
        // 8. Пробел после запятой (если пропущен)
        .replace(Regex(""",(?=\S)"""), ", ")
        // 9. Удаление пробелов в начале и конце строки
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

