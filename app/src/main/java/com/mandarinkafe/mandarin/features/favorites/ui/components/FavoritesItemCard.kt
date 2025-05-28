package com.mandarinkafe.mandarin.features.favorites.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.extensions.customizedText
import com.mandarinkafe.mandarin.core.domain.models.extensions.isCustomized
import com.mandarinkafe.mandarin.core.domain.models.extensions.totalPrice
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract.CartEvent
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract.CartState
import com.mandarinkafe.mandarin.features.favorites.ui.view_model.FavoritesContract.FavoritesEvent
import com.mandarinkafe.mandarin.features.favorites.ui.view_model.FavoritesContract.FavoritesEvent.OpenMealDetails
import com.mandarinkafe.mandarin.util.ui.components.MealItemImageBox

/**
 * Компонент для отображения товара, добавленного в избранное. Поддерживает и базовые, и кастомизированные блюда.
 */

@Composable
fun FavoritesItemCard(
    item: CustomizedMeal,
    onEvent: (FavoritesEvent) -> Unit,
    onCartEvent: (CartEvent) -> Unit,
    cartState: CartState,
) {
    val meal = item.meal

    Column(
        modifier = Modifier
            .background(Colors.AppBlack)
            .padding(horizontal = Dimens.MarginSmall8)
            .clickable(onClick = { onEvent(OpenMealDetails(item)) })
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {

            MealItemImageBox(
                modifier = Modifier
                    .size(Dimens.MealSmallImage80),
                meal = meal,
                onToggleFavorite = { onEvent(FavoritesEvent.ToggleFavorite(item)) },
            )

            Column(
                modifier = Modifier
                    .padding(
                        start = Dimens.MarginStandard16,
                        bottom = Dimens.MarginSmall8
                    )
                    .fillMaxWidth()
            ) {
                // Название блюда
                Text(
                    text = meal.name,
                    style = Typography.RegularTextStyle,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                // Описание блюда
                Text(
                    text = meal.description,
                    style = Typography.MealSmallTextStyle,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                // Выбранные опции кастомизации
                if (item.isCustomized()) {
                    Text(
                        text = item.customizedText(),
                        style = Typography.MealSmallTextStyle,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {

            // Стоимость 1 шт с учётом всех добавок и модификаторов
            Text(
                text = stringResource(R.string.meal_price_template, item.totalPrice()),
                style = Typography.MealPriceStyle,
            )

            Spacer(modifier = Modifier.weight(1f))

            // Кнопки
            FavoriteItemButtonRow(
                item = item,
                onCartEvent = onCartEvent,
                cartState = cartState,
                onFavoritesEvent = onEvent,
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.MarginSmall8),
            thickness = Dimens.DividerHeight1,
            color = Colors.LightGrey.copy(alpha = 0.2f)
        )
    }
}


