package com.mandarinkafe.mandarin.menu.ui.components.mealitem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Event

@Composable
fun MenuMealItem(
    meal: Meal,
    onEvent: (Event) -> Unit,
    onCartEvent: (CartContract.Event) -> Unit,
    cartState: CartContract.State
) {

    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .padding(Dimens.MarginSmall8)
            .clickable(onClick = { onEvent(Event.OnMealDetailsClick(meal)) })
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

            // Блок с текстовой информацией о блюде
            Column(modifier = Modifier.heightIn(min = Dimens.MealMinDescriptionHeight96)) {
                Text(
                    text = meal.name,
                    style = Typography.MealTitleStyle,
                    maxLines = 3
                )

                if (meal.description.isNotEmpty()) {
                    Text(
                        text = meal.description,
                        style = Typography.MealSmallTextStyle,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (meal.weight != 0) {
                    Text(
                        modifier = Modifier.padding(vertical = Dimens.MarginSuperSmall4),
                        text = stringResource(R.string.meal_weight_template, meal.weight),
                        style = Typography.MealSmallTextStyle
                    )
                }
            }
            // Контейнер для кнопок
            Box(contentAlignment = Alignment.BottomStart) {
                MealButtonsRow(
                    meal = meal,
                    onMenuEvent = onEvent,
                    onCartEvent = onCartEvent,
                    cartState = cartState,
                )
            }
        }
    }
}
