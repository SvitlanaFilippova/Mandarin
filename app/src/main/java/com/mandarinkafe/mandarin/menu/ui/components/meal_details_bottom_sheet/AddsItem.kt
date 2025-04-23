package com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.menu.domain.models.MealAdditional

@Composable
fun AddsItem(
    add: MealAdditional,
    onCheckedChange: (Boolean, MealAdditional) -> Unit,
    isAdded: Boolean
) {

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Checkbox(
                checked = isAdded,
                onCheckedChange = { checked ->
                    onCheckedChange(checked, add)
                },
                enabled = true,
                colors = CheckboxDefaults.colors(checkedColor = Colors.Orange)
            )
            Text(text = add.name, style = Typography.RegularTextStyle)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.meal_price_template, add.price),
                style = Typography.MealPriceStyle
            )

        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = Dimens.DividerHeight1,
            color = Colors.Grey.copy(alpha = 0.1f)
        )
    }
}