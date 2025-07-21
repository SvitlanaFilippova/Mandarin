package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.modifiers

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.ModifierItem
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.removeLeadingDash

@Composable
fun ModifierSingleSelectItem(
    item: ModifierItem,
    isAdded: Boolean,
    onItemSelected: (ModifierItem) -> Unit
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
                .height(Dimens.ModifierRowHeight48)
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

            Text(
                modifier = Modifier.padding(horizontal = Dimens.MarginSmall8),
                text = item.name.removeLeadingDash(),
                style = Typography.RegularTextStyle
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.padding(horizontal = Dimens.MarginSmall8),
                text = stringResource(R.string.meal_price_template, item.price),
                style = Typography.MealPriceStyle
            )

        }

    }
}

