package com.mandarinkafe.mandarin.core.domain.models

import com.mandarinkafe.mandarin.core.domain.models.MeasureUnitType.entries

enum class MeasureUnitType(val rawValue: String) {
    GRAM("GRAM"),
    KILOGRAM("KILOGRAM"),
    MILLILITER("MILLILITER"),
    LITER("LITER");

    companion object {
        fun from(value: String?): MeasureUnitType? {
            return entries.firstOrNull { it.rawValue.equals(value, ignoreCase = true) }
        }
    }
}