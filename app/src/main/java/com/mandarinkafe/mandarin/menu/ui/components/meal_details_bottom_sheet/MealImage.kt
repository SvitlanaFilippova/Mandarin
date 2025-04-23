package com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.Meal

@Composable
fun MealImage(meal: Meal) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = meal.imageUrl.ifEmpty { R.drawable.logo_orange_square },
            contentDescription = stringResource(R.string.picture_of_meal_template, meal.name),
            error = painterResource(R.drawable.logo_orange_square),
            placeholder = painterResource(R.drawable.logo_orange_square),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.MarginStandard16)
                .clip(RoundedCornerShape(Dimens.CornerRadius8))
                .background(
                    color = Colors.AppBlack,
                    shape = RoundedCornerShape(Dimens.CornerRadius8)
                )

        )
    }
}