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
        .replace(shortWordRegex) { matchResult ->
            nonBreakingSpace + matchResult.value
        }
        .replace("...", "…")                          // Многоточие
        .replace(Regex("""\s*-\s*"""), " — ")         // Дефис/тире как длинное тире с пробелами
        .replace("« ", "«")                           // Убираем пробел после открывающей кавычки
        .replace(" »", "»")                           // Убираем пробел перед закрывающей кавычкой
        .replace(Regex("""^-(\S)"""), "- $1")         // Пробел после дефиса в начале строки
}