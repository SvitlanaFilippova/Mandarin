package com.mandarinkafe.mandarin.menu.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.data.DtoToDomainConverter.Companion.PARENT_PIZZA_ID
import com.mandarinkafe.mandarin.menu.domain.models.Meal

@Preview
@Composable
fun ItemMenuMeal() {
    val meal =
        Meal(     //временная мок-переменная для Preview. В дальенйшем передавать meal как аргумент функции
            "1",
            "0013",
            "МАРГАРИТА",
            "Моцарелла, сливочно-чесночный соус, шампиньоны, вяленые томаты, сливочно-чесночный соус, шампиньоны, вяленые томаты, сливочно-чесночный соус, шампиньоны, вяленые томаты",
            490,
            585,
            "https://optim.tildacdn.com/tild3064-3131-4362-b537-366634323165/-/resize/312x/-/format/webp/margaritta_veg.jpg",
            "pizza",
            true,
            null, PARENT_PIZZA_ID, true

        )
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(Dimens.MarginSmall8)
    ) {
        AsyncImage(
            model = meal.imageUrl,
            contentDescription = "Изображение ${meal.name}",
            modifier = Modifier.size(Dimens.MealImage136)
        )
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = meal.name,
                style = Typography.,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            if (meal.description != null) {
                Text(
                    text = meal.description,
                    style = TextStyle(
                        color = Color.LightGray,
                        fontSize = Dimens.TextSizeSmall11
                    )
                )
            }
            if (meal.weight != null && meal.weight != 0) {
                Text(
                    text = "${meal.weight}г",
                    style = TextStyle(
                        color = Color.LightGray,
                        fontSize = Dimens.TextSizeSmall11
                    )
                )
            }
            Row {
                IconButton(onClick = { }) {

                }
                IconButton(onClick = { }) {

                }
                IconButton(onClick = { }) {

                }
            }

        }
    }
}