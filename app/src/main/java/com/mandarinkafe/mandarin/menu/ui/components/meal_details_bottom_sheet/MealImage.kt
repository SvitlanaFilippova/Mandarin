package com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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

@Composable
fun MealImage(
    modifier: Modifier = Modifier, mealImg: String, mealName: String
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = mealImg,
            contentDescription = stringResource(R.string.picture_of_meal_template, mealName),
            error = painterResource(R.drawable.logo_orange_square),
            placeholder = painterResource(R.drawable.logo_orange_square),
            modifier = modifier
                .padding(vertical = Dimens.MarginStandard16)
                .clip(RoundedCornerShape(Dimens.CornerRadius8))
                .background(
                    color = Colors.AppBlack,
                    shape = RoundedCornerShape(Dimens.CornerRadius8)
                )
        )
    }
}