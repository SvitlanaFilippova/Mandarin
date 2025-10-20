package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun NameWeightPriceRow(
    modifier: Modifier = Modifier,
    name: String,
    weight: Int,
    measureUnit: String?,
    price: Int
) {
    Row(
        modifier = modifier
            .padding(end = Dimens.MarginSmall8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Название (переносы)
        Text(
            modifier = Modifier
                .padding(
                    start = Dimens.MarginStandard16,
                    top = Dimens.MarginSmall8,
                    bottom = Dimens.MarginSmall8
                )
                .weight(1f, fill = true),
            text = name,
            style = Typography.RegularTextStyle,
            softWrap = true
        )

        // Вес (рядом с названием)
        if (weight != 0) {
            Text(
                modifier = Modifier.padding(horizontal = Dimens.MarginSmall8),
                text = stringResource(
                    MR.strings.meal_weight_template_for_adds,
                    weight,
                    measureUnit ?: ""
                ),
                style = Typography.RegularExtraLightTextStyle,
            )
        }

        // Цена (в конце)
        Text(
            modifier = Modifier
                .widthIn(min = Dimens.PriceMinWidthForCustomizeItem)
                .padding(end = Dimens.MarginSuperSmall4),
            textAlign = TextAlign.End,
            text = stringResource(MR.strings.meal_price_template, price),
            style = Typography.MealPriceStyle
        )
    }
}
