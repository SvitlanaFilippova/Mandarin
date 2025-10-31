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
