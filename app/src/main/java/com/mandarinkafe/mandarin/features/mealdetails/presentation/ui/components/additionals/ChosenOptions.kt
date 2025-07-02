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
                        itemName = item.name,
                        onClick = { onModifierClick(group, item) }
                    )
                }
            }
            adds.forEach {
                ChosenItemChip(
                    itemName = it.name,
                    onClick = { onAddClick(it) }
                )
            }
        }
    }
}