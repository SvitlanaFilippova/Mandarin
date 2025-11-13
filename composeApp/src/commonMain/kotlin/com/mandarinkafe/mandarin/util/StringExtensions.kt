package com.mandarinkafe.mandarin.util

import com.mandarinkafe.mandarin.util.Constants.VALID_PHONE_LENGTH


/**
 * Заменяет все нестандартные пробелы на обычные (для правильной работы поиска)
 */
fun String.normalize(): String =
    this
        .replace(Regex("\\u00A0"), " ") // неразрывный
        .replace(Regex("\\s+"), " ") // любые пробелы/табы → один
        .trim()

fun String.toTranslitVariants(): List<String> {
    val original = this
    val toCyrillic = this.toCyrillicTranslit()
    val toLatin = this.toLatinTranslit()
    return listOf(original, toCyrillic, toLatin)
}

fun String.fuzzyContains(query: String): Boolean {
    val normalized = this.lowercase()
    val q = query.lowercase()
    return normalized.contains(q) || levenshtein(normalized, q) <= 1
}

// Классическая метрика Левенштейна
private fun levenshtein(lhs: String, rhs: String): Int {
    val dp = Array(lhs.length + 1) { IntArray(rhs.length + 1) }

    for (i in lhs.indices + 1) dp[i][0] = i
    for (j in rhs.indices + 1) dp[0][j] = j

    for (i in 1..lhs.length) {
        for (j in 1..rhs.length) {
            val cost = if (lhs[i - 1] == rhs[j - 1]) 0 else 1
            dp[i][j] = minOf(
                dp[i - 1][j] + 1,
                dp[i][j - 1] + 1,
                dp[i - 1][j - 1] + cost
            )
        }
    }

    return dp[lhs.length][rhs.length]
}

fun String.levenshteinDistance(other: String): Int {
    val lhs = this.lowercase()
    val rhs = other.lowercase()

    if (lhs == rhs) return 0
    if (lhs.isEmpty()) return rhs.length
    if (rhs.isEmpty()) return lhs.length

    val dp = Array(lhs.length + 1) { IntArray(rhs.length + 1) }

    for (i in 0..lhs.length) dp[i][0] = i
    for (j in 0..rhs.length) dp[0][j] = j

    for (i in 1..lhs.length) {
        for (j in 1..rhs.length) {
            val cost = if (lhs[i - 1] == rhs[j - 1]) 0 else 1
            dp[i][j] = minOf(
                dp[i - 1][j] + 1,
                dp[i][j - 1] + 1,
                dp[i - 1][j - 1] + cost
            )
        }
    }

    return dp[lhs.length][rhs.length]
}

private val latinToCyrillicMap = mapOf(
    "a" to "а", "b" to "б", "v" to "в", "g" to "г", "d" to "д",
    "e" to "е", "yo" to "ё", "zh" to "ж", "z" to "з", "i" to "и", "y" to "й",
    "k" to "к", "l" to "л", "m" to "м", "n" to "н", "o" to "о", "p" to "п",
    "r" to "р", "s" to "с", "t" to "т", "u" to "у", "f" to "ф", "h" to "х",
    "ts" to "ц", "ch" to "ч", "sh" to "ш", "shch" to "щ", "yu" to "ю", "ya" to "я"
)

private val cyrillicToLatinMap = latinToCyrillicMap.entries.associate { (k, v) -> v to k }

fun String.toCyrillicTranslit(): String {
    var result = this.lowercase()
    // Пробегаем по длинным ключам сначала (shch > sh > s)
    latinToCyrillicMap.toList().sortedByDescending { it.first.length }.forEach { (latin, cyr) ->
        result = result.replace(latin, cyr)
    }
    return result
}

fun String.toLatinTranslit(): String {
    var result = this.lowercase()
    cyrillicToLatinMap.toList().sortedByDescending { it.first.length }.forEach { (cyr, latin) ->
        result = result.replace(cyr, latin)
    }
    return result
}

fun String?.toVisibleComment(): String {
    if (this == null) return ""
    val dividers = listOf(COMMENT_DIVIDER_1, COMMENT_DIVIDER_2)
    val firstDividerIndex = dividers
        .mapNotNull { divider -> indexOf(divider).takeIf { it >= 0 } }
        .minOrNull()

    return if (firstDividerIndex != null) {
        substring(0, firstDividerIndex).trim()
    } else {
        this.trim()
    }
}

private const val COMMENT_DIVIDER_1 = "\\\\"
private const val COMMENT_DIVIDER_2 = "//"


/**
 * Форматирует номер телефона из формата 9299964288, 89991234567, 79991234567 или +79991234567
 * в формат +7 (929) 996–42–88
 * @return отформатированный номер телефона в формате +7 (XXX) XXX–XX–XX
 */
fun String.formatPhoneNumberForUi(): String {
    // Оставляем только цифры и знак + для обработки +7
    val normalized = this.replace("+", "").filter { it.isDigit() }

    // Нормализуем номер: убираем префиксы 8, 7 если номер 11 цифр
    val digitsOnly = when {
        normalized.length == PHONE_LENGTH_WITH_PREFIX && normalized.startsWith(PHONE_PREFIX_8) -> {
            // Номер начинается с 8 (например, 89991234567) -> убираем 8
            normalized.drop(1)
        }

        normalized.length == PHONE_LENGTH_WITH_PREFIX && normalized.startsWith(PHONE_PREFIX_7) -> {
            // Номер начинается с 7 (например, 79991234567) -> убираем 7
            normalized.drop(1)
        }

        normalized.length == PHONE_LENGTH_DIGITS -> {
            // Номер уже 10 цифр без префикса
            normalized
        }

        else -> {
            // Некорректная длина, возвращаем как есть
            return this
        }
    }

    // Проверяем, что после нормализации осталось 10 цифр
    if (digitsOnly.length != PHONE_LENGTH_DIGITS) {
        return this
    }

    // Разбиваем на группы: код города (3), основная часть (3), и две группы по 2
    val areaCode = digitsOnly.substring(
        PHONE_AREA_CODE_START,
        PHONE_AREA_CODE_START + PHONE_AREA_CODE_LENGTH
    )
    val firstPart = digitsOnly.substring(
        PHONE_FIRST_PART_START,
        PHONE_FIRST_PART_START + PHONE_FIRST_PART_LENGTH
    )
    val secondPart = digitsOnly.substring(
        PHONE_SECOND_PART_START,
        PHONE_SECOND_PART_START + PHONE_SECOND_PART_LENGTH
    )
    val thirdPart = digitsOnly.substring(
        PHONE_THIRD_PART_START,
        PHONE_THIRD_PART_START + PHONE_THIRD_PART_LENGTH
    )

    return "$PHONE_PREFIX_RU$PHONE_NON_BREAKING_SPACE" +
            "$PHONE_AREA_CODE_BRACKET_OPEN$areaCode$PHONE_AREA_CODE_BRACKET_CLOSE" +
            "$PHONE_NON_BREAKING_SPACE$firstPart" +
            "$PHONE_SEPARATOR$secondPart$PHONE_SEPARATOR$thirdPart"
}

/**
 * Форматирует номер телефона для использования в доменных API (без префикса +7).
 * 
 * Принимает номер в различных форматах:
 * - 10 цифр: "9299964288" → "9299964288"
 * - 11 цифр с префиксом 8: "89991234567" → "9991234567"
 * - 11 цифр с префиксом 7: "79991234567" → "9991234567"
 * - С префиксом +7: "+79991234567" → "9991234567"
 * 
 * @return Номер телефона в формате "XXXXXXXXXX" (10 цифр без префикса +7)
 *         Если номер не может быть нормализован до 10 цифр, возвращает исходную строку
 */
fun String.formatPhoneNumberForDomain(): String {
    val rawPhone = this
    val digitsOnly = rawPhone.filter { it.isDigit() }
    val normalized = when {
        digitsOnly.startsWith(PHONE_PREFIX_7) -> digitsOnly.drop(1)
        digitsOnly.startsWith(PHONE_PREFIX_8) -> digitsOnly.drop(1)
        else -> digitsOnly
    }
    val phone = normalized.take(VALID_PHONE_LENGTH)
    return phone
}

/**
 * Форматирует номер телефона для использования в внешних SDK (например, YooKassa SDK).
 * 
 * Принимает номер в различных форматах:
 * - 10 цифр: "9299964288" → "+79299964288"
 * - 11 цифр с префиксом 8: "89991234567" → "+79991234567"
 * - 11 цифр с префиксом 7: "79991234567" → "+79991234567"
 * - С префиксом +7: "+79991234567" → "+79991234567"
 * 
 * @return Номер телефона в международном формате "+7XXXXXXXXXX" (12 символов: +7 + 10 цифр)
 *         Если номер не может быть нормализован до 10 цифр, возвращает исходную строку
 */
fun String.formatPhoneNumberForSdk(): String {
    val rawPhone = this
    // Оставляем только цифры (убираем все символы кроме цифр, включая +, пробелы, скобки и т.д.)
    val digitsOnly = rawPhone.filter { it.isDigit() }
    
    // Нормализуем: убираем префиксы 7 или 8, если номер 11 цифр
    val normalized = when {
        digitsOnly.length == PHONE_LENGTH_WITH_PREFIX && digitsOnly.startsWith(PHONE_PREFIX_7) -> {
            // Номер начинается с 7 (например, 79991234567) -> убираем 7
            digitsOnly.drop(1)
        }
        digitsOnly.length == PHONE_LENGTH_WITH_PREFIX && digitsOnly.startsWith(PHONE_PREFIX_8) -> {
            // Номер начинается с 8 (например, 89991234567) -> убираем 8
            digitsOnly.drop(1)
        }
        digitsOnly.length == VALID_PHONE_LENGTH -> {
            // Номер уже 10 цифр без префикса
            digitsOnly
        }
        else -> {
            // Некорректная длина, возвращаем как есть
            return this
        }
    }
    
    // Проверяем, что после нормализации осталось 10 цифр
    if (normalized.length != VALID_PHONE_LENGTH) {
        return this
    }
    
    // Возвращаем в формате +7XXXXXXXXXX
    return "$PHONE_PREFIX_RU$normalized"
}

private const val PHONE_PREFIX_RU = "+7"
private const val PHONE_PREFIX_7 = "7"
private const val PHONE_PREFIX_8 = "8"
private const val PHONE_LENGTH_DIGITS = 10
private const val PHONE_LENGTH_WITH_PREFIX = 11
private const val PHONE_AREA_CODE_LENGTH = 3
private const val PHONE_FIRST_PART_LENGTH = 3
private const val PHONE_SECOND_PART_LENGTH = 2
private const val PHONE_THIRD_PART_LENGTH = 2
private const val PHONE_AREA_CODE_START = 0
private const val PHONE_FIRST_PART_START = 3
private const val PHONE_SECOND_PART_START = 6
private const val PHONE_THIRD_PART_START = 8
private const val PHONE_AREA_CODE_BRACKET_OPEN = "("
private const val PHONE_AREA_CODE_BRACKET_CLOSE = ")"
private const val PHONE_SEPARATOR = "–"
private const val PHONE_NON_BREAKING_SPACE = "\u00A0"
