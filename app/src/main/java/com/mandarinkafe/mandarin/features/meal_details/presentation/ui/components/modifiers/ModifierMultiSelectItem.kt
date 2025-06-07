package com.mandarinkafe.mandarin.features.meal_details.presentation.ui.components.modifiers

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.ModifierItem
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.removeLeadingDash

@Composable
fun ModifierMultiSelectItem(
    item: ModifierItem,
    onCheckedChange: (Boolean) -> Unit,
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
                .clickable { onCheckedChange(!isAdded) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Checkbox(
                modifier = Modifier.padding(
                    horizontal = Dimens.Margin12,
                ),
                checked = isAdded,
                onCheckedChange = null, // обработка клика происходит в Row
                enabled = true,
                colors = CheckboxDefaults.colors(checkedColor = Colors.Orange)
            )
            Text(
                modifier = Modifier.padding(horizontal = Dimens.MarginStandard16),
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
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = Dimens.DividerHeight1,
            color = Colors.LightGrey.copy(alpha = 0.1f)
        )
    }
}