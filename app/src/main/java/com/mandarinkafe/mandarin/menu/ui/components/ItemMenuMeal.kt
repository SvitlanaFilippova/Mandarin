package com.mandarinkafe.mandarin.menu.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.menu.data.DtoToDomainConverter.Companion.PARENT_PIZZA_ID
import com.mandarinkafe.mandarin.menu.domain.models.Meal

@Preview
@Composable
fun ItemMenuMeal() {
    //переменная для отслеживания состояния длинных описаний
    var isDescExpanded by remember {
        mutableStateOf(false)
    }
    var isNameExpanded by remember {
        mutableStateOf(false)
    }
    val meal =
        //временная мок-переменная для Preview. В дальенйшем передавать meal как аргумент функции
        Meal(
            "1",
            "0013",
            "МАРГАРИТА С ВЯЛЕНЫМИ ТОМАТАМИ И ПЕРЧИКАМИ ЧОРИЗЗО КОПЧЁНЫМИ НА ВОЛОСАХ ДЕВСТВЕНИЦЫ",
            "Моцарелла, сливочно-чесночный соус, шампиньоны, вяленые томаты, сливочно-чесночный соус, шампиньоны, вяленые томаты, сливочно-чесночный соус, шампиньоны, вяленые томаты, ещё моцарелла, сливочно-чесночный соус, шампиньоны, вяленые томаты, сливочно-чесночный соус, шампиньоны, вяленые томаты, сливочно-чесночный соус, шампиньоны, вяленые томаты, и ещё больше моцареллы, сливочно-чесночный соус, шампиньоны, вяленые томаты, сливочно-чесночный соус, шампиньоны, вяленые томаты, сливочно-чесночный соус, шампиньоны, вяленые томаты",
            490,
            585,
            "https://optim.tildacdn.com/tild3064-3131-4362-b537-366634323165/-/resize/312x/-/format/webp/margaritta_veg.jpg",
            "pizza",
            false,
            null, PARENT_PIZZA_ID, true

        )
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start,
    ) {
        Image(
//временое мок-изображение для Preview. В дальенйшем будет AsyncImage
            painter = painterResource(R.drawable.margaritta_veg),
            contentDescription = "Превью изображения",
            modifier = Modifier
                .size(Dimens.MealImage136)
                .clip(shape = RectangleShape),
            contentScale = ContentScale.Crop
        )
//        AsyncImage(
//            model = meal.imageUrl.ifEmpty { R.drawable.logo_orange },
//            contentDescription = "Изображение ${meal.name}",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier.size(Dimens.MealImage136)
//        )
        Column(
            modifier = Modifier.padding(start = Dimens.MarginSmall8),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = meal.name,
                style = Typography.MealTitleStyle,
                maxLines = if (isNameExpanded) Int.MAX_VALUE else Int.MAX_VALUE,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { isNameExpanded = !isNameExpanded }
            )
            if (meal.description != null) {
                Text(
                    text = meal.description,
                    maxLines = if (isDescExpanded) Int.MAX_VALUE else Int.MAX_VALUE,
                    overflow = TextOverflow.Ellipsis,
                    style = Typography.MealSmallTextStyle,
                    modifier = Modifier.clickable { isDescExpanded = !isDescExpanded }
                )
            }
            if (meal.weight != null && meal.weight != 0) {
                Text(
                    text = "${meal.weight}г",
                    style = Typography.MealSmallTextStyle
                )
            }

            ButtonsRow(meal)
        }
    }
}

@Composable
fun ButtonsRow(meal: Meal) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.MarginStandard16),
        modifier = Modifier.padding(top = Dimens.MarginSmall8)
    ) {
        // Кнопка "Добавить в корзину"
        Button(
            onClick = { /* Действие */ },
            modifier = Modifier
                .width(Dimens.ButtonToCartBig120)
                .height(Dimens.ButtonToCartSmall32),
            shape = RoundedCornerShape(Dimens.ButtonRadius8),
            colors = ButtonDefaults.buttonColors(
                containerColor = Colors.Orange,
                contentColor = Color.White
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_cart),
                    contentDescription = "Добавить в корзину",
                    modifier = Modifier.size(Dimens.IconSize16),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(Dimens.MarginSuperSmall4))
                Text(
                    text = "${meal.price} ₽",
                    style = TextStyle(fontSize = Dimens.TextSizeRegular14),
                    color = Color.White
                )
            }
        }

        // Кнопка "Редактировать"
        IconButton(
            onClick = { /* Действие */ },
            modifier = Modifier.size(Dimens.ButtonToCartSmall32)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_additionals),
                contentDescription = "Редактировать",
                modifier = Modifier.size(Dimens.ButtonEditMeal32),
                tint = Color.Unspecified
            )
        }

        // Кнопка "Избранное"
        IconButton(
            onClick = { /* Действие */ },
            modifier = Modifier.size(Dimens.ButtonToCartSmall32)
        ) {
            Icon(
                painter = painterResource(
                    if (meal.isFavorite) R.drawable.ic_favorite_active
                    else R.drawable.ic_favorite_inactive
                ),
                contentDescription = "Добавить в избранное",
                modifier = Modifier.size(Dimens.ButtonToggleFavorite28),
                tint = Color.Unspecified
            )
        }
    }
}


