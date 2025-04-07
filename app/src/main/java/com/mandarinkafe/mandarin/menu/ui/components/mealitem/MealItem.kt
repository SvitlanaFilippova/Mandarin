package com.mandarinkafe.mandarin.menu.ui.components.mealitem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.ui.MenuContract.Event
import com.mandarinkafe.mandarin.util.ui.components.ExpandableText

@Composable
fun MenuMealItem(
    meal: Meal,
    onEvent: (Event) -> Unit
) {
    //переменные для отслеживания состояния длинных описаний и названий
    var isNameExpanded by remember {
        mutableStateOf(false)
    }
    var isDescriptionExpanded by remember {
        mutableStateOf(false)
    }
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(Dimens.MarginSmall8)
    ) {

        AsyncImage(
            model = meal.imageUrl.ifEmpty { R.drawable.logo_orange_square },
            contentDescription = stringResource(R.string.picture_of_meal_template, meal.name),
            error = painterResource(R.drawable.logo_orange_square),
            placeholder = painterResource(R.drawable.logo_orange_square),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(Dimens.MealImage136)
                .clip(RoundedCornerShape(Dimens.CornerRadius8))
                .background(
                    color = Colors.AppBlack,
                    shape = RoundedCornerShape(Dimens.CornerRadius8)
                )
        )

        Column(
            modifier = Modifier
                .padding(start = Dimens.MarginSmall8)
        )
        {
            // Блок с текстовой информацией
            Column(modifier = Modifier.heightIn(min = Dimens.MealMinDescriptionHeight96)) {
                ExpandableText(
                    text = meal.name,
                    style = Typography.MealTitleStyle,
                    isExpanded = isNameExpanded,
                    onClick = { isNameExpanded = !isNameExpanded },
                    maxLinesCollapsed = 3
                )

                if (meal.description != null) {
                    ExpandableText(
                        text = meal.description,
                        style = Typography.MealSmallTextStyle,
                        isExpanded = isDescriptionExpanded,
                        onClick = { isDescriptionExpanded = !isDescriptionExpanded },
                        maxLinesCollapsed = 4
                    )
                }
                if (meal.weight != null && meal.weight != 0) {
                    Text(
                        text = stringResource(R.string.meal_weight_template, meal.weight),
                        style = Typography.MealSmallTextStyle
                    )
                }
            }
            // Контейнер для кнопок
            Box(contentAlignment = Alignment.BottomStart) {
                MealButtonsRow(
                    meal = meal,
                    onEvent = onEvent
                )
            }
        }
    }
}
