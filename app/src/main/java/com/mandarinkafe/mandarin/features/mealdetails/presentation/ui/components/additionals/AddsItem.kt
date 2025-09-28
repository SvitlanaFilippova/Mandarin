package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.additionals

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun AddsItem(
    add: MealAdditional,
    onCheckedChange: (Boolean, MealAdditional) -> Unit,
    isAdded: Boolean
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
                    onValueChange = { onCheckedChange(!isAdded, add) },
                    role = Role.Checkbox
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Checkbox(
                modifier = Modifier.padding(
                    start = Dimens.Margin12,
                    end = Dimens.MarginSuperSmall4
                ),
                checked = isAdded,
                onCheckedChange = null, // обработка клика происходит в Row
                enabled = true,
                colors = CheckboxDefaults.colors(checkedColor = Colors.Orange)
            )
            Text(
                modifier = Modifier.padding(horizontal = Dimens.MarginStandard16),
                text = add.name,
                style = Typography.RegularTextStyle
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.padding(Dimens.MarginSmall8),
                text = stringResource(R.string.meal_price_template, add.price),
                style = Typography.MealPriceStyle
            )

        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = Dimens.DividerHeight1,
            color = Colors.LightGrey.copy(alpha = 0.1f)
        )
    }
}