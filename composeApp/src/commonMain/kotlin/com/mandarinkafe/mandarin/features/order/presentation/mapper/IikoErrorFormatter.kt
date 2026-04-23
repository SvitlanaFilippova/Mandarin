package com.mandarinkafe.mandarin.features.order.presentation.mapper

object IikoErrorFormatter {
    private val uuidRegex = Regex("""\s*\([0-9a-fA-F-]{36}\)""")
    private val productRegex = Regex("""[“"](.+?)[”"]""")

    fun format(message: String?): String? {
        if (message.isNullOrBlank()) return null

        val cleaned = message
            .replace(uuidRegex, "")
            .replace("\\s+".toRegex(), " ")
            .trim()

        val product = productRegex
            .find(cleaned)
            ?.groupValues
            ?.getOrNull(1)
            ?.trim()

        if (product != null) {
            return buildString {
                append(product)
                append(" временно недоступен")
            }
        }

        return cleaned
            .replace("Product", "")
            .replace("is excluded from menu", "временно недоступен. ")
            .replace("for order's", "")
            .replace(".", "")
            .trim()
    }
}