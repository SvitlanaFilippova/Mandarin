package com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.menu.domain.models.Meal

@Composable
fun MealInfo(
    meal: Meal,
) {
    Column {
        if (meal.description.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(vertical = Dimens.MarginStandard16),
                text = meal.description,
                style = Typography.RegularTextStyle
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (meal.weight != 0) {
                Text(
                    text = stringResource(R.string.meal_weight_template, meal.weight),
                    style = Typography.RegularTextStyle
                )
            }
            Text(
                text = stringResource(R.string.meal_price_template, meal.price),
                style = Typography.MealPriceStyle
            )
        }

    }
}