package com.mandarinkafe.mandarin.search.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.ui.components.mealitem.MealButtonsRow
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Event

@Composable
fun SearchResultsMealItem(
    meal: Meal,
    onEvent: (Event) -> Unit,
    onItemClick: (Meal) -> Unit,
    onCartEvent: (CartContract.Event) -> Unit,
    cartState: CartContract.State,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = Dimens.MarginSmall8)
            .fillMaxWidth()
            .clickable { onItemClick(meal) }
    ) {

        AsyncImage(
            model = meal.imageUrl.ifEmpty { R.drawable.logo_orange_square },
            contentDescription = stringResource(R.string.picture_of_meal_template, meal.name),
            error = painterResource(R.drawable.logo_orange_square),
            placeholder = painterResource(R.drawable.logo_orange_square),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(Dimens.MealSmallImage64)
                .clip(RoundedCornerShape(Dimens.CornerRadius8))
                .background(
                    color = Colors.AppBlack,
                    shape = RoundedCornerShape(Dimens.CornerRadius8)
                )
        )

        Column(
            modifier = Modifier
                .padding(start = Dimens.MarginSmall8),
        )
        {
            Text(
                text = meal.name,
                style = Typography.MealTitleStyle,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3
            )

            MealButtonsRow(
                meal = meal,
                onEvent = onEvent,
                onCartEvent = onCartEvent,
                cartState = cartState,
            )
        }
    }
}