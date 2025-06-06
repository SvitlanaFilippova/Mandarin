package com.mandarinkafe.mandarin.features.meal_details.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import coil3.compose.AsyncImage
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.features.search.SearchMapper.toUiModel
import com.mandarinkafe.mandarin.util.ui.components.LabelChip

@Composable
fun MealInfo(
    meal: Meal,
) {
    Log.d("DEBUG IMAGES", "Meal: ${meal.name}, image: ${meal.imageUrl}")

    Column {
        // Изображение блюда
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = meal.imageUrl,
                contentDescription = stringResource(
                    R.string.picture_of_meal_template,
                    meal.name
                ),
                error = painterResource(R.drawable.placeholder_meal_no_photo),
                placeholder = painterResource(R.drawable.placeholder_meal_no_photo),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(vertical = Dimens.MarginSmall8)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(Dimens.CornerRadius8))
            )

            // Ярлыки
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = Dimens.MarginStandard16)
            ) {
                meal.labels.forEach {
                    LabelChip(
                        label = it.toUiModel(),
                    )
                }
            }
        }

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
                    text = stringResource(R.string.meal_weight_template, meal.weight),
                    style = Typography.RegularLightTextStyle
                )
            }
        }
    }
}