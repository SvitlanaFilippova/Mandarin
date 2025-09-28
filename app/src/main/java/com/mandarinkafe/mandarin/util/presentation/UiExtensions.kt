package com.mandarinkafe.mandarin.util.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.MeasureUnitType

@Composable
fun MeasureUnitType.localizedShortText(): String {
    val resId = when (this) {
        MeasureUnitType.GRAM -> R.string.unit_gram
        MeasureUnitType.KILOGRAM -> R.string.unit_kg
        MeasureUnitType.MILLILITER -> R.string.unit_ml
        MeasureUnitType.LITER -> R.string.unit_l
    }
    return stringResource(resId)
}