package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.modifiers

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.ModifierItem
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.Constants.MIN_MODIFIER_LIMIT_TO_IGNORE

@Composable
fun ModifierGroupItem(
    modifierGroup: ModifierGroup,
    chosenModifiers: List<ModifierGroup>,
    onChooseSingleModifier: (ModifierGroup) -> Unit,
    onChooseMultiModifiers: (
        modifierGroup: ModifierGroup,
        modifierItem: ModifierItem,
        isChecked: Boolean
    ) -> Unit

) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(Dimens.MarginSmall8)
    ) {
        Text(
            text = modifierGroup.name,
            style = Typography.TitleStyle
        )
        if (modifierGroup.isRequired) {
            Text(
                modifier = Modifier.padding(start = Dimens.MarginSmall8),
                text = stringResource(R.string.mandatory),
                style = Typography.RegularTextStyle,
                fontWeight = FontWeight.Light
            )
        }

        if (modifierGroup.maxQuantity in 2 until MIN_MODIFIER_LIMIT_TO_IGNORE) {
            Text(
                modifier = Modifier.padding(start = Dimens.MarginSmall8),
                text = stringResource(R.string.maximum_modifier_short, modifierGroup.maxQuantity),
                style = Typography.RegularTextStyle,
                fontWeight = FontWeight.Light,
            )
        }
    }
    val selectedItem =
        chosenModifiers.find { it.id == modifierGroup.id }?.items?.getOrNull(
            0
        )
    0

    if (modifierGroup.isSingleChoice) {
        modifierGroup.items.forEach { item ->
            ModifierSingleSelectItem(
                item = item,
                isAdded = item == selectedItem,
                onItemSelected = { item ->
                    onChooseSingleModifier(
                        modifierGroup.copy(
                            items = listOf(
                                item
                            )
                        )
                    )
                }
            )
        }
    } else {
        modifierGroup.items.forEach { item ->
            ModifierMultiSelectItem(
                item = item,
                onCheckedChange = { isChecked ->
                    onChooseMultiModifiers(
                        modifierGroup,
                        item,
                        isChecked
                    )
                },
                isAdded = chosenModifiers.find { it.id == modifierGroup.id }?.items?.contains(
                    item
                ) == true
            )
        }
        Spacer(modifier = Modifier.height(Dimens.MarginBig24))
    }
}