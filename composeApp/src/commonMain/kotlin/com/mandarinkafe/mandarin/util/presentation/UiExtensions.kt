package com.mandarinkafe.mandarin.util.presentation

import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.MeasureUnitType
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun MeasureUnitType.localizedShortText(): String {
    val resId = when (this) {
        MeasureUnitType.GRAM -> MR.strings.unit_gram
        MeasureUnitType.KILOGRAM -> MR.strings.unit_kg
        MeasureUnitType.MILLILITER -> MR.strings.unit_ml
        MeasureUnitType.LITER -> MR.strings.unit_l
    }
    return stringResource(resId)
}

/**
 * Форматирует вес для отображения.
 * Для дробных значений (литры) форматирует как дробное число, для целых - как целое.
 */
@Composable
fun formatWeight(weight: Float, measureUnitType: MeasureUnitType): String {
    if (weight == 0f) return ""

    // Проверяем, является ли значение целым числом
    val isWholeNumber = weight % 1f == 0f

    return if (isWholeNumber) {
        // Для целых чисел используем шаблон с %d
        stringResource(
            MR.strings.meal_weight_template,
            weight.toInt(),
            measureUnitType.localizedShortText()
        )
    } else {
        // Для дробных чисел форматируем и убираем лишние нули в конце
        val formatted = formatFloat(weight)
        stringResource(
            MR.strings.meal_weight_template_float,
            formatted,
            measureUnitType.localizedShortText()
        )
    }
}

/**
 * Форматирует Float с максимум 2 знаками после запятой, убирая лишние нули.
 */
private fun formatFloat(value: Float): String {
    // Округляем до 2 знаков после запятой
    val decimalPlacesMultiplier = 100f
    val rounded = (value * decimalPlacesMultiplier).toInt().toFloat() / decimalPlacesMultiplier

    // Преобразуем в строку
    val stringValue = rounded.toString()

    // Если есть точка, убираем лишние нули в конце
    return if (stringValue.contains('.')) {
        stringValue.trimEnd('0').trimEnd('.')
    } else {
        stringValue
    }
}
