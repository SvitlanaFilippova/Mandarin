package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.additionals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.ModifierItem
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.presentation.localizedShortText

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
            text = stringResource(id = R.string.chosen),
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
                            measureUnit = item.measureUnitType.localizedShortText()
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
                        measureUnit = it.measureUnitType.localizedShortText()
                    ),
                    onClick = { onAddClick(it) }
                )
            }
        }
    }
}

/** Форматирует название + вес (если вес есть) */
private fun formatNameWithWeight(name: String, weight: Int, measureUnit: String): String {
    return if (weight > 0) "$name, $weight $measureUnit" else name
}