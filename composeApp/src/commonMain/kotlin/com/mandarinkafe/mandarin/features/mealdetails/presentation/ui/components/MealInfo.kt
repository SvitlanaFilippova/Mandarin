package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.presentation.formatWeight
import com.mandarinkafe.mandarin.util.presentation.ui.components.TooltipText
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun MealInfo(
    meal: Meal,
) {
    Column {
        MealDetailsImage(meal)
        if (meal.description.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(vertical = Dimens.MarginSmall8),
                text = meal.description,
                style = Typography.RegularLightTextStyle,
                fontWeight = FontWeight.Light,
                color = Colors.LightGrey
            )
        } else {
            Spacer(modifier = Modifier.height(Dimens.MarginStandard16))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            val weightText = formatWeight(meal.weight, meal.measureUnitType)
            if (weightText.isNotEmpty()) {
                Text(
                    text = weightText,
                    style = Typography.RegularLightTextStyle
                )
            }
        }

        // Метка "только самовывоз"
        if (meal.isPickupOnly) {
            TooltipText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.MarginSmall8),
                text = stringResource(MR.strings.for_selfpickup_details)
            )
        }
    }
}