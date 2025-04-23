package com.mandarinkafe.mandarin.cart.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.menu.domain.models.Meal

/**
 * Компонент, который отвечает за отображение товара, который выбрали в меню
 */

@Composable
fun itemCart(itenCartMeals: Meal) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(R.color.black))
            .padding(bottom = dimensionResource(R.dimen.standart_margin_16))
    ) {
        // Используем AsyncImage из Coil для загрузки изображения из URL
        AsyncImage(
            model = itenCartMeals.imageUrl,
            contentDescription = "Изображекние блюда",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(136.dp)
                .padding(end = dimensionResource(R.dimen.small_margin_8))
        )
        Column(
            modifier = Modifier
                .padding(
                    start = dimensionResource(R.dimen.dr_cursor_wight_2),
                    bottom = dimensionResource(R.dimen.dr_cursor_wight_2)
                )
                .fillMaxWidth()
        ) {
            //Название блюда
            Text(
                text = itenCartMeals.name,
                style = Typography.RegularTextStyle,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth(),

                )
            //Ингридиенты блюда
            if (itenCartMeals.description != null) {
                Text(
                    text = itenCartMeals.description,
                    style = Typography.MealSmallTextStyle,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            // Вес блюда
            if (itenCartMeals.weight != null) {
                Text(
                    text = "${itenCartMeals.weight}г",
                    style = Typography.MealSmallTextStyle,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            // Цена блюда
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(Dimens.RadiusSearchField8))
                    .background(Colors.Orange)
            ) {
                Text(
                    text = "${itenCartMeals.price}р.",
                    style = Typography.MealPriceStyle,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

        }
    }
}
