package com.mandarinkafe.mandarin.util

object TypographyRules {
    /**
     * Список коротких слов (предлогов, союзов и частиц), перед которыми нужен неразрывный пробел
     */
    val shortWords = listOf(
        "и", "в", "во", "не", "на", "за", "из", "от",
        "по", "о", "об", "а", "с", "у", "к", "до"
    )
}

fun String.applyTypography(): String {
    val nonBreakingSpace = "\u00A0"

    val shortWordRegex = Regex(
        """(?<=\s)(${TypographyRules.shortWords.joinToString("|")})(?=\s)""",
        RegexOption.IGNORE_CASE
    )

    return this
        // 1. Исправляем дефис без пробела после переноса
        .replace(Regex("""(?<=^|\s)-(?=\S)""")) { "- " }
        // 2. Неразрывный пробел перед короткими словами
        .replace(shortWordRegex) { matchResult ->
            nonBreakingSpace + matchResult.value
        }
        // 3. Многоточие
        .replace("...", "…")
        // 4. Тире с пробелами
        .replace(Regex("""\s*-\s*"""), " — ")
        // 5. Кавычки-ёлочки
        .replace("« ", "«")
        .replace(" »", "»")
}