package com.mandarinkafe.mandarin.menu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.menu.data.DtoToDomainConverter.Companion.PARENT_PIZZA_ID
import com.mandarinkafe.mandarin.menu.domain.models.Meal

@Preview
@Composable
fun ItemMenuMealPreview() {
    val meal =
        Meal(
            id = "1",
            sku = "0013",
            name = "МАРГАРИТА С ВЯЛЕНЫМИ ТОМАТАМИ",
            description = "Томатный соус, ветчина, маринованные огурцы, ветчина, маринованные огурцы, ветчина, маринованные огурцы, ветчина, маринованные огурцы, ветчина, маринованные огурцы, ветчина, маринованные огурцы, ветчина, маринованные огурцы, ветчина, маринованные огурцы, ветчина, маринованные огурцы, ветчина, маринованные огурцы, ветчина, маринованные огурцы",
            weight = 490,
            price = 2585,
            imageUrl = "https://optim.tildacdn.com/tild3064-3131-4362-b537-366634323165/-/resize/312x/-/format/webp/margaritta_veg.jpg",
            categoryId = "pizza",
            isFavorite = false,
            tags = null,
            topCategoryId = PARENT_PIZZA_ID,
            isEditable = true
        )
    MenuMealItem(meal)
}

@Composable
fun MenuMealItem(meal: Meal) {
    //переменная для отслеживания состояния длинных описаний
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
            contentDescription = "Изображение ${meal.name}",
            error = painterResource(R.drawable.logo_orange_square),
            placeholder = painterResource(R.drawable.logo_orange_square),
            contentScale = ContentScale.Crop, // Обрезает изображение, сохраняя пропорции
            modifier = Modifier
                .size(Dimens.MealImage136) // Фиксированный квадратный размер
                .clip(RoundedCornerShape(Dimens.ButtonRadius8)) // Скругление углов
                .background(
                    color = Colors.AppBlack,
                    shape = RoundedCornerShape(Dimens.ButtonRadius8) // Скругление для фона
                )
        )

        Column(
            modifier = Modifier
                .padding(start = Dimens.MarginSmall8)
        )
        {
            // Верхний блок с текстом
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
        modifier = Modifier
            .padding(top = Dimens.MarginSmall8)
            .fillMaxWidth()

    ) {

        // Кнопки корзины
        Row(
            modifier = Modifier
                .widthIn(min = Dimens.ButtonToCartBig120)
                .height(Dimens.ButtonToCartSmall32)

        ) {
            if (!isInTheCart) {
                // Кнопка "Добавить в корзину"
                Button(
                    onClick = {
                        isInTheCart = true
                    },
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

                //Доп кнопки, когда товар в корзине
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
        if (meal.isEditable) {
            // Кнопка "Редактировать"
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
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
            }
        } else {
            // Если кнопка редактирования скрыта, добавляем пустой Box, чтобы "Избранное" оставалось справа
            Spacer(modifier = Modifier.weight(1f))
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

@Composable
fun ExpandableText(
    text: String,
    style: TextStyle,
    isExpanded: Boolean,
    onClick: () -> Unit,
    maxLinesCollapsed: Int
) {
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val isTextOverflow = textLayoutResult?.hasVisualOverflow ?: false

    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .wrapContentHeight()
    ) {
        Text(
            text = text,
            style = style,
            maxLines = if (isExpanded) Int.MAX_VALUE else maxLinesCollapsed,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
            onTextLayout = { result ->
                textLayoutResult = result
            }
        )

        if (!isExpanded && isTextOverflow) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .drawWithContent {
                        val gradientHeight = size.height * 0.5f
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Colors.AppBlack.copy(alpha = 0.8f)
                                ),
                                startY = size.height - gradientHeight,
                                endY = size.height
                            ),
                            topLeft = Offset(0f, size.height - gradientHeight),
                            size = Size(size.width, gradientHeight)
                        )
                    }
            )

            // 3. Иконка стрелки (отдельный слой поверх градиента)
            Box(
                modifier = Modifier
                    .matchParentSize()
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_down),
                    contentDescription = "Раскрыть текст",
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomCenter)
                        .offset(y = (8).dp),
                    tint = Colors.Grey
                )
            }
        }
    }
}
