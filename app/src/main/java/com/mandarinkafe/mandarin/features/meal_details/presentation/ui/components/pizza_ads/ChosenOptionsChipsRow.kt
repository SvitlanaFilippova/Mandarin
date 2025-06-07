package com.mandarinkafe.mandarin.features.meal_details.presentation.ui.components.pizza_ads

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.ModifierItem

@Composable
fun ChosenOptionsChipsRow(
    adds: List<MealAdditional>,
    onAddClick: (MealAdditional) -> Unit,
    modifiers: List<ModifierGroup>,
    onModifierClick: (ModifierGroup, ModifierItem) -> Unit,
) {
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