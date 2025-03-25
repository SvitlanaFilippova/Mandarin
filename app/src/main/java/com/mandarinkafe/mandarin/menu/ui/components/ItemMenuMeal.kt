package com.mandarinkafe.mandarin.menu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.menu.data.DtoToDomainConverter.Companion.PARENT_PIZZA_ID
import com.mandarinkafe.mandarin.menu.domain.models.Meal

@Preview
@Composable
fun PreviewItemMenuMeal() {
    val meal =
        Meal(
            "1",
            "0013",
            "МАРГАРИТА С ВЯЛЕНЫМИ ТОМАТАМИ И ПЕРЧИКАМИ ЧОРИЗЗО КОПЧЁНЫМИ НА ВОЛОСАХ ДЕВСТВЕНИЦЫ",
            "Томатный соус, ветчина, маринованные огурцы, ветчина, маринованные огурцы, ветчина, маринованные огурцы, ветчина, маринованные огурцы, ветчина, маринованные огурцы, ветчина, маринованные огурцы, ветчина, маринованные огурцы, ветчина, маринованные огурцы, ветчина, маринованные огурцы, ветчина, маринованные огурцы, ветчина, маринованные огурцы",
            490,
            2585,
            "https://optim.tildacdn.com/tild3064-3131-4362-b537-366634323165/-/resize/312x/-/format/webp/margaritta_veg.jpg",
            "pizza",
            false,
            null, PARENT_PIZZA_ID, true

        )
    ItemMenuMeal(meal)
}

@Composable
fun ItemMenuMeal(meal: Meal) {
    //переменная для отслеживания состояния длинных описаний
    var isNameExpanded by remember {
        mutableStateOf(false)
    }
    var isDescriptionExpanded by remember {
        mutableStateOf(false)
    }
    Row(
        verticalAlignment = Alignment.Top
    ) {
//        Image(
////временое мок-изображение для Preview. В дальенйшем будет AsyncImage
//            painter = painterResource(R.drawable.margaritta_veg),
//            contentDescription = "Превью изображения",
//            modifier = Modifier
//                .size(Dimens.MealImage136)
//                .clip(shape = RectangleShape),
//            contentScale = ContentScale.Crop
//        )
        AsyncImage(
            model = meal.imageUrl.ifEmpty { R.drawable.logo_orange },
            contentDescription = "Изображение ${meal.name}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(Dimens.MealImage136)
                .clip(shape = RectangleShape),
        )
        Column(
            modifier = Modifier
                .padding(start = Dimens.MarginSmall8)
        )
        {
            // Верхний блок с текстом
            Column(modifier = Modifier.heightIn(min = Dimens.MealMinDescriptionHeight96)) {
                // fill=false, чтобы высота была по контенту
                Text(
                    text = meal.name,
                    style = Typography.MealTitleStyle,
                    maxLines = if (isNameExpanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable { isNameExpanded = !isNameExpanded }
                )
                if (meal.description != null) {
                    Text(
                        text = meal.description,
                        maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 4,
                        overflow = TextOverflow.Ellipsis,
                        style = Typography.MealSmallTextStyle,
                        modifier = Modifier.clickable {
                            isDescriptionExpanded = !isDescriptionExpanded
                        }
                    )
                }
                if (meal.weight != null && meal.weight != 0) {
                    Text(
                        text = "${meal.weight}г",
                        style = Typography.MealSmallTextStyle
                    )
                }
            }
            // Контейнер для кнопок, чтобы они прижимались вниз
            Box(contentAlignment = Alignment.BottomStart) {
                ButtonsRow(meal)
            }
        }
    }

}

@Composable
fun ButtonsRow(meal: Meal) {
    var isInTheCart by remember {
        mutableStateOf(false)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.MarginStandard16),
        modifier = Modifier.padding(top = Dimens.MarginSmall8)
    ) {

        Row(
            modifier = Modifier
                .widthIn(min = Dimens.ButtonToCartBig120)
                .height(Dimens.ButtonToCartSmall32)

        ) {
            if (!isInTheCart) {
                // Кнопка "Добавить в корзину"
                Button(
                    onClick = { isInTheCart = true },
                    shape = RoundedCornerShape(Dimens.ButtonRadius8),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Colors.Orange,
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens.MarginStandard16),

                        ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_cart),
                            contentDescription = "Добавить в корзину",
                            tint = Color.White
                        )

                        Text(
                            text = "${meal.price} ₽",
                            style = Typography.ToCartButtonStyle,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Visible
                        )
                    }
                }
            } else {

                var numberInCart by remember { mutableIntStateOf(1) }
                val sumInCart = numberInCart * meal.price

                Box(
                    modifier = Modifier
                        .width(Dimens.ButtonToCartBig120)
                        .height(Dimens.ButtonToCartSmall32)
                        .clip(RoundedCornerShape(Dimens.ButtonRadius8))
                        .background(Colors.GreyTransparent10)
                ) {
                    Row(
                        modifier = Modifier
                            .matchParentSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Кнопка "-"
                        IconButton(
                            onClick = {
                                if (numberInCart > 0) {
                                    numberInCart--
                                    isInTheCart = numberInCart > 0
                                }
                            },
                            modifier = Modifier.size(Dimens.ButtonToCartSmall32)
                        ) {
                            Text(
                                text = "-",
                                style = Typography.ToCartButtonStyle,
                                color = Color.White
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Количество товара в корзине
                            Text(
                                text = "$numberInCart шт",
                                style = Typography.CartButtonSmallTextStyle,
                            )
                            // Общая сумма
                            Text(
                                text = "$sumInCart ₽",
                                style = Typography.CartButtonSmallTextStyle,
                                color = Colors.WhiteTransparent75
                            )
                        }

                        // Кнопка "+"
                        IconButton(
                            onClick = { numberInCart++ },
                            modifier = Modifier.size(Dimens.ButtonToCartSmall32)
                        ) {
                            Text(
                                text = "+",
                                style = Typography.ToCartButtonStyle,
                                color = Color.White
                            )
                        }
                    }
                }
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


