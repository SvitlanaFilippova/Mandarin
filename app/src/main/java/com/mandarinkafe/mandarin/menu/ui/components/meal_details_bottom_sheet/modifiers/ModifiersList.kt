package com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet.modifiers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifiersList(
    availableModifiers: List<ModifierGroup>,
    onModifierSelected: (ModifierGroup) -> Unit,
    chosenModifiers: List<ModifierGroup>

) {
    Column(
        modifier = Modifier
            .background(Colors.AppBlack)
    ) {
        availableModifiers.forEach { modifierGroup ->
            Text(
                modifier = Modifier.padding(vertical = Dimens.MarginSmall8),
                text = modifierGroup.name,
                style = Typography.RegularTextStyle
            )

            ModifierSingleSelector(
                items = modifierGroup.items,
                selectedItem = chosenModifiers.find { it.id == modifierGroup.id }?.items?.get(0),
                onItemSelected = { item ->
                    onModifierSelected(modifierGroup.copy(items = listOf(item)))
                }
            )
            Spacer(modifier = Modifier.height(Dimens.MarginStandard16))
        }
    }
}