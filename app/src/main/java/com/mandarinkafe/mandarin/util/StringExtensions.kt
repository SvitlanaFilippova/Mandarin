package com.mandarinkafe.mandarin.util

/**
 * Заменяет все нестандартные пробелы на обычные (для правильной работы поиска)
 */
fun String.normalize(): String =
    this
        .replace(Regex("\\u00A0"), " ")   // неразрывный
        .replace(Regex("\\s+"), " ")      // любые пробелы/табы → один
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