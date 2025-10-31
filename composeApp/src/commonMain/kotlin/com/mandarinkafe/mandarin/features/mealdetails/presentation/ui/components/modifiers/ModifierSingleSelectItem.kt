package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.modifiers

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import com.mandarinkafe.mandarin.core.domain.models.ModifierItem
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.NameWeightPriceRow
import com.mandarinkafe.mandarin.util.presentation.localizedShortText
import com.mandarinkafe.mandarin.util.removeLeadingDash

@Composable
fun ModifierSingleSelectItem(
    item: ModifierItem,
    isAdded: Boolean,
    onItemSelected: (ModifierItem) -> Unit,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isAdded) Colors.Orange.copy(alpha = 0.1f) else Color.Transparent,
        label = "AddHighlight"
    )

    Column(
        modifier = Modifier
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = Dimens.ModifierRowHeight48)
                .toggleable(
                    value = isAdded,
                    onValueChange = { onItemSelected(item) },
                    role = Role.RadioButton
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RadioButton(
                modifier = Modifier.padding(horizontal = Dimens.Margin12),
                selected = isAdded,
                colors = RadioButtonDefaults.colors(selectedColor = Colors.Orange),
                onClick = null // обработка клика происходит в Row
            )
            NameWeightPriceRow(
                modifier = Modifier.weight(1f),
                name = item.name.removeLeadingDash(),
                weight = item.weight,
                measureUnit = item.measureUnitType.localizedShortText(),
                price = item.price
            )
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = Dimens.DividerHeight1,
            color = Colors.LightGrey.copy(alpha = 0.1f)
        )
    }
}

