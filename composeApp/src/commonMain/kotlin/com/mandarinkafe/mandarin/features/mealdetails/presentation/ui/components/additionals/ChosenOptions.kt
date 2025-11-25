package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.additionals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.ModifierItem
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.presentation.formatWeight
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ChosenOptions(
    adds: List<MealAdditional>,
    onAddClick: (MealAdditional) -> Unit,
    modifiers: List<ModifierGroup>,
    onModifierClick: (ModifierGroup, ModifierItem) -> Unit,
) {
    Column {
        Text(
            modifier = Modifier.padding(
                top = Dimens.MarginBig24,
                bottom = Dimens.MarginSmall8
            ),
            text = stringResource(MR.strings.chosen),
            style = Typography.RegularLightTextStyle,
            fontWeight = FontWeight.Light,
            color = Colors.LightGrey
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
        ) {
            modifiers.forEach { group ->
                group.items.forEach { item ->
                    ChosenItemChip(
                        itemName = formatNameWithWeight(
                            name = item.name,
                            weight = item.weight,
                            measureUnitType = item.measureUnitType
                        ),
                        onClick = { onModifierClick(group, item) }
                    )
                }
            }
            adds.forEach {
                ChosenItemChip(
                    itemName = formatNameWithWeight(
                        name = it.name,
                        weight = it.weight,
                        measureUnitType = it.measureUnitType
                    ),
                    onClick = { onAddClick(it) }
                )
            }
        }
    }
}

/** Форматирует название + вес (если вес есть) */
@Composable
private fun formatNameWithWeight(
    name: String,
    weight: Float,
    measureUnitType: com.mandarinkafe.mandarin.core.domain.models.MeasureUnitType,
): String {
    val weightText = formatWeight(weight, measureUnitType)
    return if (weightText.isNotEmpty()) "$name, $weightText" else name
}