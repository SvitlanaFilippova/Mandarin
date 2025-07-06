package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components

import android.util.Log
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.presentation.localizedShortText

@Composable
fun MealInfo(
    meal: Meal,
) {
    Log.d("DEBUG IMAGES", "Meal: ${meal.name}, image: ${meal.imageUrl}")

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
            if (meal.weight != 0) {
                Text(
                    text = stringResource(
                        R.string.meal_weight_template,
                        meal.weight,
                        meal.measureUnitType.localizedShortText()
                    ),
                    style = Typography.RegularLightTextStyle
                )
            }
        }

        // Метка "только самовывоз"
        if (meal.isPickupOnly) {
            Text(
                modifier = Modifier.padding(vertical = Dimens.MarginSmall8),
                text = stringResource(R.string.for_selfpickup_details),
                style = Typography.RegularLightTextStyle,
                fontWeight = FontWeight.Light,
                color = Colors.LightGrey
            )
        }
    }
}